package com.example.zac.movieroster.data;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.zac.movieroster.BuildConfig;
import com.example.zac.movieroster.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

/**
 * Created by Zac on 9/30/16.
 */

public class FetchMovies extends AsyncTask<String, Void, MovieData[]> {

    private final String LOG_TAG = FetchMovies.class.getSimpleName();

    private Realm myRealm;
    private String categoryParam;

    private int dayOfYear;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    protected MovieData[] doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        //get our day of the year to pass into the database so we can check to see if we've already pulled data for the day
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -2);

        //past date is 2 months in the past
        String pastDate = format.format(calendar.getTime());

        //Add 3 months back to get the date from two months in the future
        calendar.add(Calendar.MONTH, 4);

        //future date is 2 months in the future
        String futureDate = format.format(calendar.getTime());

        try {
            categoryParam = params[0];
            Log.v(LOG_TAG, "OUR PARAMS FOR THE FETCHING OF TRAILERS " + categoryParam);

            String URLAppend = categoryParam.equals("popular")
                    ? "/discover/movie?language=en-US&sort_by=popularity.desc&include_adult=false&primary_release_date.gte=" + pastDate + "&primary_release_date.lte=" + futureDate
                    : "/discover/movie?vote_count.gte=200&sort_by=vote_average.desc";

            final String DATABASE_BASE_URL = "http://api.themoviedb.org/3";
            final String API_ADDON = "&api_key=";

            final String API_KEY = BuildConfig.API_KEY;

            URL url = new URL(DATABASE_BASE_URL + URLAppend + API_ADDON + API_KEY);

            Log.v(LOG_TAG, "URL = " + url);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.v(LOG_TAG, "our input stream has no data - null inputStream");
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


        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the poster data, there's no point in attempting
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
            Log.v(LOG_TAG, "try method with getMovieDataFromJson ran");
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(MovieData[] result) {
        if (result != null) {


            for (final MovieData movieDataInfo : result) {
                myRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Movies movie = realm.createObject(Movies.class);
                        movie.setMovieId(movieDataInfo.getMovieId());
                        movie.setMovieBackDrop(movieDataInfo.getMovieBackDrop());
                        movie.setMoviePosterPath(movieDataInfo.getMoviePosterPath());
                        movie.setMovieReleaseDate(movieDataInfo.getMovieReleaseDate());
                        movie.setMovieSynopsis(movieDataInfo.getMovieSynopsis());
                        movie.setMovieTitle(movieDataInfo.getMovieTitle());
                        movie.setMovieUserRating(movieDataInfo.getMovieUserRating());
                        movie.setCategory(categoryParam);
                        movie.setDayOfYear(dayOfYear);
                    }
                });
            }
        }
    }

    private MovieData[] getMovieDataFromJson(String movieJsonStr) throws JSONException {
        final String JSON_RESULTS = "results";
        final String JSON_TITLE = "original_title";
        final String JSON_POSTER = "poster_path";
        final String JSON_OVERVIEW = "overview";
        final String JSON_RELEASE = "release_date";
        final String JSON_RATING = "vote_average";
        final String JSON_BACKDROP = "backdrop_path";
        final String JSON_ID = "id";

        JSONObject movieJSON = new JSONObject(movieJsonStr);
        JSONArray moviesArray = movieJSON.getJSONArray(JSON_RESULTS);

        MovieData[] resultMovieInfo = new MovieData[moviesArray.length()];


        for (int i=0; i < moviesArray.length(); i++) {

            String title;
            String poster;
            String synopsis;
            String releaseUnformatted;
            String rating;
            String backdrop;
            String id;
            String release = "null";


            //Get the JSON object for the movie
            JSONObject movieInformation = moviesArray.getJSONObject(i);

            //get all information to pass into the MovieData Object
            title = movieInformation.getString(JSON_TITLE);
            poster = movieInformation.getString(JSON_POSTER);
            synopsis = movieInformation.getString(JSON_OVERVIEW);
            releaseUnformatted = movieInformation.getString(JSON_RELEASE);
            rating = movieInformation.getString(JSON_RATING);
            backdrop = movieInformation.getString(JSON_BACKDROP);
            id = movieInformation.getString(JSON_ID);

            //format the date before entry
            //Our desired date format
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date unformattedDate = format.parse(releaseUnformatted);
                format = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                release = format.format(unformattedDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Add new MovieData Object into resultMovieInfo list
            resultMovieInfo[i] = new MovieData(title, poster, synopsis, release, rating, backdrop, id, "0");
        }
        return resultMovieInfo;
    }
}

