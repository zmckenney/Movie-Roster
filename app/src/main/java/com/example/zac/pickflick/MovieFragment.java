package com.example.zac.pickflick;

import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    public static ArrayAdapter<String> mMoviesAdapter;

    public MovieFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            updateMovies();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String loc = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        moviesTask.execute();
        Log.v(LOG_TAG, "updateMovies() was called");
        Log.v(LOG_TAG, "resultStrs in update = " + mMoviesAdapter);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);


        mMoviesAdapter = new ArrayAdapter<String>(
                //Context (Frags Parent Activity)
                getActivity(),
                //ID of grid layout
                R.layout.list_item_movies,
                //ID of imageView to populate
                R.id.list_item_movie_textview,
                //Poster Data
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
        listView.setAdapter(mMoviesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String moviePosition = mMoviesAdapter.getItem(i);
                Toast.makeText(getActivity(), moviePosition, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;


    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private String[] getMovieDataFromJson(String movieJsonStr) throws JSONException {

            final String JSON_RESULTS = "results";
            final String JSON_TITLE = "title";
            final String JSON_POSTER = "poster_path";
            final String JSON_OVERVIEW = "overview";

            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = forecastJson.getJSONArray(JSON_RESULTS);

            String[] resultStr = new String[20];
            for (int i=0; i < moviesArray.length(); i++){

                String title;
                String poster;
                String overview;

                //Get the JSON object for the movie
                JSONObject movieInformation = moviesArray.getJSONObject(i);

                title = movieInformation.getString(JSON_TITLE);
                poster = movieInformation.getString(JSON_POSTER);
                overview = movieInformation.getString(JSON_OVERVIEW);

                resultStr[i] = title + " " + poster;


            }

            return resultStr;
        }

        @Override
        protected String[] doInBackground(String... params) {


            //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-==-=-=-=-=-=-=-=-=-=-=-=-=-=-=--==--=-=-=-=-=-=
            //Check all the code below - copy pasta from Sunshine


            /**
             *
             *
             *
             *
            //If there arent any params then theres nothing to do so null
            if (params.length == 0) {
                return null;
            }


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            //String format = "json";
            //String units = "metric";
            //int numDays = 7;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                //              STATIC CODE BELOW - NEED TO MAKE IT AN INPUT
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?zip=44720&mode=json&units=metric&cnt=7");
/**
 final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
 final String QUERY_PARAM = "zip";
 final String FORMAT_PARAM = "mode";
 final String UNITS_PARAM = "units";
 final String DAYS_PARAM = "cnt";

 Uri.Builder builtURI = Uri.parse(FORECAST_BASE_URL).buildUpon()
 .appendQueryParameter(QUERY_PARAM, params[0])
 .appendQueryParameter(FORMAT_PARAM, format)
 .appendQueryParameter(UNITS_PARAM, units)
 .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays));


 */
/**
                //TODO: TMDb API Key is required below to grab json data
                //URL url = new URL(builtURI.toString());
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=");

                //Log.v(LOG_TAG, "Built URI: " + builtURI.toString());


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
 */

            //TODO: Commented out the null params if statement because wouldnt run if used
            //If there arent any params then theres nothing to do so null
            //if (params.length == 0) {
            //    Log.v(LOG_TAG, "The Params for our doInBackground method are null, no Params!");
            //    return null;
            //}

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;


            try {
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=");
                Log.v(LOG_TAG, "URL properly used");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG,"our input stream has no data - null inputStream");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON string : " + movieJsonStr);


            //=-==-=--=-=-==-=-=-=-=-=-=-=-=-=-=-==-=--=-==--=-=-=All Code Below is for the Catch, get the top working first!!!! =-==-=-=-=-=-=--=-=
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mMoviesAdapter.clear();
                for (String movieTitleStr : result) {
                    mMoviesAdapter.add(movieTitleStr);
                }
            }

        }

    }
}
