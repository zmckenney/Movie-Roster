package com.example.zac.moviepicks;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    public static final String DETAILFRAGMENT_TAG = "DFTAG";

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private PosterAdapter mPosterAdapter;

    private ArrayList<MovieData> movieDataResults = new ArrayList<>();

    private ArrayList<MovieData> movieFinalResults;

    String URLAppend;
    String[] moviePassDatas;

    //The minimum amount of votes needed for a movie to be eligible for the Top Rated list.  Maybe put this in a settings menu later?
    String voteMinimum = "50";

    //Most Popular listed = 0, Highest Rated = 1, Favorite = 2
    public static int popRateOrFavorites = 0;


    //Values changed upon action bar clicks, used to prevent API data pulls from occurring every time the button is used
    int popularClicked = 0;
    int ratingClicked = 0;
    int favoritesClicked = 0;

    public Realm myRealm;
    private RealmChangeListener realmChangeListener;


    public MovieFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myRealm = Realm.getDefaultInstance();
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                if (popRateOrFavorites == 2) {
                    mPosterAdapter.clear();
                    searchAndSetFavs(false);

                }
            }
        };
        myRealm.addChangeListener(realmChangeListener);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieFinalResults = new ArrayList<>(movieDataResults);

        }
        else {
            movieFinalResults = savedInstanceState.getParcelableArrayList("movies");
        }

        if (movieFinalResults.size() == 0) {
            switch (popRateOrFavorites) {

                case 0:
                    URLAppend = "/discover/movie?sort_by=popularity.desc";
                    updateMovies();
                    Log.v(LOG_TAG, "movieFinalResults is 0");
                    break;

                case 1:
                    URLAppend = "/discover/movie?vote_count.gte=" + voteMinimum + "&sort_by=vote_average.desc";
                    updateMovies();
                    Log.v(LOG_TAG, "movieFinalResults is 0");
                    break;

                case 2:
                    searchAndSetFavs(true);
                    Log.v(LOG_TAG, "movieFinalResults is 0");
                    break;
            }

        }
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
        Log.v(LOG_TAG, "" + item);
        int id = item.getItemId();

        switch (id) {
        //    case R.id.action_refresh :
        //        updateMovies();
        //        break;

            case R.id.action_popular :
                    if (popularClicked == 0){

                        popularClicked = 1;
                        ratingClicked = 0;
                        favoritesClicked = 0;

                        popRateOrFavorites = 0;
                    URLAppend = "/discover/movie?sort_by=popularity.desc";
                    Log.v(LOG_TAG, "Most Popular tapped, URL = " + URLAppend);
                    updateMovies();
                    //Log.v(LOG_TAG, "ID for this action " + item);
                    }
                break;

            case R.id.action_rating :
                    if (ratingClicked == 0){

                        ratingClicked = 1;
                        popularClicked = 0;
                        favoritesClicked = 0;

                        popRateOrFavorites = 1;
                    URLAppend = "/discover/movie?vote_count.gte=" + voteMinimum + "&sort_by=vote_average.desc";
                    Log.v(LOG_TAG, "Rating tapped, URL = " + URLAppend);
                    updateMovies();
                    }

                break;

            case R.id.action_favorites :
                if (favoritesClicked == 0){

                    favoritesClicked = 1;
                    Log.v("@#$@#$@#$@#$@#", "FAVORITES CLICKED AND FAVORITESCLICKED = " + favoritesClicked);
                    popularClicked = 0;
                    favoritesClicked = 0;

                    popRateOrFavorites = 2;

                    mPosterAdapter.clear();
                    searchAndSetFavs(false);

                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void searchAndSetFavs(boolean init) {
        RealmResults<Favorite> results1 =
            myRealm.where(Favorite.class).findAll();

        if (init) {
            Log.v("@#$@#$@#$@#$", "THIS IS THE AMOUNT OF RESULTS WE HAVE " + results1.size());
            for (Favorite c : results1){
                movieFinalResults.add(new MovieData(c.getTitle(), c.getPosterPath(), c.getSynopsis(), c.getReleaseDate(), c.getUserRating(), c.getBackDrop(), c.getMovieId(), "1"));
            }

        } else {
            Log.v("@#$@#$@#$@#$", "THIS IS THE AMOUNT OF RESULTS WE HAVE " + results1.size());
            for (Favorite c : results1) {
                mPosterAdapter.add(new MovieData(c.getTitle(), c.getPosterPath(), c.getSynopsis(), c.getReleaseDate(), c.getUserRating(), c.getBackDrop(), c.getMovieId(), "1"));
            }
        }
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
        Log.v(LOG_TAG, "updateMovies() was called");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieFinalResults);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_main, container, false);
        mPosterAdapter = new PosterAdapter(getActivity(), movieFinalResults);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.poster_gridview);

        gridView.setAdapter(mPosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieData moviePosition = mPosterAdapter.getItem(i);
                String title = moviePosition.movieTitle;
                String poster = moviePosition.moviePosterPath;
                String synopsis = moviePosition.movieSynopsis;
                String release = moviePosition.movieReleaseDate;
                String rating = moviePosition.movieUserRating;
                String backdrop = moviePosition.movieBackDrop;
                String movieId = moviePosition.movieId;

                Log.v(LOG_TAG, "moviePosition items are being called : " + moviePosition.movieTitle + " - " + moviePosition.movieId );

//                MainActivity mainActivity = new MainActivity();
//
//                mainActivity.passMovieDetails(moviePosition);


//                if (MainActivity.twoPaneView == true){

                moviePassDatas = new String[]{title, poster, synopsis, release, rating, backdrop, movieId};

                    Bundle args = new Bundle();
//                    args.putParcelable(DetailFragment.DETAIL_DATA, moviePosition);
                    args.putStringArray(MainActivity.DETAILFRAGMENT_DATA, moviePassDatas);


                //If we are in a 2 pane view, replace the current fragment with the updated fragment
                if (MainActivity.twoPaneView) {
                    DetailFragment df = new DetailFragment();
                    df.setArguments(args);

                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.movie_detail_container, df, DETAILFRAGMENT_TAG).commit();
                }

                //If we are NOT in 2 pane View, send to DetailActivity to create a new Fragment with the arguments from the intent
                else if (!MainActivity.twoPaneView) {
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class).putExtra(DetailActivity.DETAIL_INTENT_PASS, args);
                    startActivity(detailIntent);
                }
            }
        });

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

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
                String release;
                String rating;
                String backdrop;
                String id;

                //Get the JSON object for the movie
                JSONObject movieInformation = moviesArray.getJSONObject(i);

                //get all information to pass into the MovieData Object
                title = movieInformation.getString(JSON_TITLE);
                poster = movieInformation.getString(JSON_POSTER);
                synopsis = movieInformation.getString(JSON_OVERVIEW);
                release = movieInformation.getString(JSON_RELEASE);
                rating = movieInformation.getString(JSON_RATING);
                backdrop = movieInformation.getString(JSON_BACKDROP);
                id = movieInformation.getString(JSON_ID);



                //Add new MovieData Object into resultMovieInfo list
                resultMovieInfo[i] = new MovieData(title, poster, synopsis, release, rating, backdrop, id, "0");
            }

            return resultMovieInfo;

        }


        @Override
        protected MovieData[] doInBackground(String... params) {

//            if (popRateOrFavorites == 2) {
//
//                final RealmResults<Favorite> results1 =
//                        myRealm.where(Favorite.class).findAll();
//
//
//                MovieData[] favMovies = new MovieData[results1.size()];
//                Log.v("@#$@#$@#$@#$", "THIS IS THE AMOUNT OF RESULTS WE HAVE " + results1.size());
//
//                for (int i = 0; i < results1.size(); i++){
//                    Favorite fav = results1.get(i);
//                    favMovies[i] = new MovieData(fav.getTitle(),fav.getPosterPath(), fav.getSynopsis(), fav.getReleaseDate(), fav.getUserRating(), fav.getBackDrop(), fav.getMovieId(), "1");
//                }
//
//
//                return favMovies;
//            }
//            else {

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String movieJsonStr = null;


                try {

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

//            }
                return null;
            }



        @Override
        protected void onPostExecute(MovieData[] result) {
            if (result != null) {
                mPosterAdapter.clear();
                for (MovieData movieDataInfo : result) {
                    mPosterAdapter.add(movieDataInfo);
            }
        }
        }
    }
}
