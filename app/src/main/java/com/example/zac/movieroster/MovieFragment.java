package com.example.zac.movieroster;

import android.content.Intent;
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

import com.example.zac.movieroster.data.Favorite;
import com.example.zac.movieroster.data.FetchMovies;
import com.example.zac.movieroster.data.Movies;

import java.util.ArrayList;
import java.util.Calendar;

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

    String[] moviePassDatas;

    //The minimum amount of votes needed for a movie to be eligible for the Top Rated list.  Maybe put this in a settings menu later?

    //Most Popular listed = 0, Highest Rated = 1, Favorite = 2
    private int category;
    private final int POPULAR = 0;
    private final int HIGHEST_RATED = 1;
    private final int FAVORITES = 2;

    private int dayOfYear;

    public Realm myRealm;


    public MovieFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRealm = Realm.getDefaultInstance();

        Calendar calendar = Calendar.getInstance();
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        RealmChangeListener realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object element) {
                switch (category) {
                    case POPULAR:
                        mPosterAdapter.clear();
                        searchAndSetMovies(false, "popular");
                        break;
                    case HIGHEST_RATED:
                        mPosterAdapter.clear();
                        searchAndSetMovies(false, "highest_rated");
                        break;
                    case FAVORITES:
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
            switch (category) {
                case POPULAR:
                        searchAndSetMovies(true, "popular");
                    break;

                case HIGHEST_RATED:
                        searchAndSetMovies(true, "highest_rated");
                    break;

                case FAVORITES:
                    searchAndSetFavs(true);
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
            case R.id.action_popular :
                category = POPULAR;
                mPosterAdapter.clear();
                searchAndSetMovies(false, "popular");
                break;

            case R.id.action_rating :
                category = HIGHEST_RATED;
                mPosterAdapter.clear();
                searchAndSetMovies(false, "highest_rated");
                break;

            case R.id.action_favorites :
                category = FAVORITES;
                mPosterAdapter.clear();
                searchAndSetFavs(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    //used to populate the favorites view with saved data - Dataset passed into the posteradapter or if posteradapter already filled, clear and refill with the new data
    private void searchAndSetFavs(boolean init) {
        RealmResults<Favorite> results1 =
            myRealm.where(Favorite.class).findAll();

        if (init) {
            for (Favorite c : results1){
                movieFinalResults.add(new MovieData(c.getTitle(), c.getPosterPath(), c.getSynopsis(), c.getReleaseDate(), c.getUserRating(), c.getBackDrop(), c.getMovieId(), "1"));
            }

        } else {
            for (Favorite c : results1) {
                mPosterAdapter.add(new MovieData(c.getTitle(), c.getPosterPath(), c.getSynopsis(), c.getReleaseDate(), c.getUserRating(), c.getBackDrop(), c.getMovieId(), "1"));
            }
        }
    }

    private void searchAndSetMovies(boolean init, String category) {
        RealmResults<Movies> results1 = myRealm.where(Movies.class).equalTo("category", category).equalTo("dayOfYear", dayOfYear).findAll();

        if (results1.isEmpty()){
            updateMovies(category);
        } else if (!results1.isEmpty()) {
            //use this to either pass data into the adapter or the data to initially be used by the adapter since mPosterAdapter will still be null
            if (init) {
                for (Movies c : results1) {
                    movieFinalResults.add(new MovieData(c.getMovieTitle(), c.getMoviePosterPath(), c.getMovieSynopsis(), c.getMovieReleaseDate(), c.getMovieUserRating(), c.getMovieBackDrop(), c.getMovieId(), "0"));
                }
            } else {
                for (Movies c : results1) {
                    mPosterAdapter.add(new MovieData(c.getMovieTitle(), c.getMoviePosterPath(), c.getMovieSynopsis(), c.getMovieReleaseDate(), c.getMovieUserRating(), c.getMovieBackDrop(), c.getMovieId(), "0"));
                }
            }

        }
    }


    private void updateMovies(String category) {
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute(category);
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

                moviePassDatas = new String[]{title, poster, synopsis, release, rating, backdrop, movieId};

                    Bundle args = new Bundle();
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
}

