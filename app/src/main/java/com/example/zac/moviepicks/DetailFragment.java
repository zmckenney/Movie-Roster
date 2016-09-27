package com.example.zac.moviepicks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import io.realm.RealmResults;

/**
 * Created by Zac on 2/1/16.
 */
public class DetailFragment extends android.support.v4.app.Fragment {

    public final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String SAVED_TRAILERS = "SAVEDTRAILERS";
    static final String SAVED_REVIEWS = "SAVEDREVIEWS";

    private String[] mMovieData;

    //Strings to be passed in via arguments
    String title;
    String poster;
    String synopsis;
    String release;
    String rating;
    String backdrop;
    String movieId;

    //Argument variables that are passed in have their views instantiated below
    private TextView mMovieTitle;
    private TextView mMovieSynopsis;
    private TextView mMovieRelease;
    private RatingBar mMovieRatingBar;
    private ImageView backDropImageView;
    private ImageView posterImageView;
    private Button buttonFavorite;

    //Instantiate Fetched Review Views
    private TextView mMovieReviewText;
    private TextView mMovieReviewText2;
    private TextView mMovieReviewText3;

    private TextView mMovieReviewAuthor;
    private TextView mMovieReviewAuthor2;
    private TextView mMovieReviewAuthor3;

    private View mReviewLine1;
    private View mReviewLine2;

    private Button buttonReadmore;
    private Button buttonReadmore2;
    private Button buttonReadmore3;

    private ArrayList<ReviewsTrailerData> savedReviews = new ArrayList<>();
    private ArrayList<ReviewsTrailerData> savedTrailers = new ArrayList<>();

    //Instantiate Fetched Trailer Views
    private TextView mMovieTrailerTextview1;
    private TextView mMovieTrailerTextview2;
    private TextView mMovieTrailerTextview3;

    private ImageView mMovieTrailerPlayIcon1;
    private ImageView mMovieTrailerPlayIcon2;
    private ImageView mMovieTrailerPlayIcon3;

    private LinearLayout mTrailerLinearLayout2;
    private LinearLayout mTrailerLinearLayout3;

    private View mTrailerLine1;
    private View mTrailerLine2;


    //Instantiate Fetched Trailer Strings
    String mTrailerPath = "0";
    String mTrailerPath2 = "0";
    String mTrailerPath3 = "0";

    String mTrailerName;
    String mTrailerName2;
    String mTrailerName3;

    //Instantiate Fetched Review Strings
    String mReviewPath = "0";
    String mReviewPath2 = "0";
    String mReviewPath3 = "0";

    String mReviewAuthor;
    String mReviewAuthor2;
    String mReviewAuthor3;

    String mReviewText;
    String mReviewText2;
    String mReviewText3;

    public Realm myRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myRealm = Realm.getDefaultInstance();

            try {
                Bundle arguments = getArguments();

                if (arguments != null) {
                    mMovieData = arguments.getStringArray(MainActivity.DETAILFRAGMENT_DATA);
                }

                title = mMovieData[0];
                poster = mMovieData[1];
                synopsis = mMovieData[2];
                release = mMovieData[3];
                rating = mMovieData[4];
                backdrop = mMovieData[5];
                movieId = mMovieData[6];

                Log.v(LOG_TAG, "MOVIE ID IN DETAIL FRAGMENT = :  " + movieId);

                //Fetch Data for the reviews and trailers only if there isnt a saved instance
                if (savedInstanceState == null) {
                    Log.v(LOG_TAG, "DetailFragment savedINstanceState is null : alsdkfjlaksdfjlkasjdf");

                    new FetchTrailersTask().execute("1");
                    new FetchTrailersTask().execute("2");
                }

                //Only pass data into savedTrailers and savedReviews, onCreateView needs to run first to create all the views before we can run updateViews
                else if (savedInstanceState != null){
                    savedTrailers = savedInstanceState.getParcelableArrayList(SAVED_TRAILERS);
                    savedReviews = savedInstanceState.getParcelableArrayList(SAVED_REVIEWS);
                }


                //This is OK to catch when using a tablet, as soon as an item is selected it will properly run the above code
            } catch (Exception e) {
                Log.v(LOG_TAG, "NO DETAILS DATA TO DISPLAY YET - SELECT AN ITEM OR FIX THE CODE TO CONTINUE");

            }
        }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        //Save the reviews and trailer data
        outState.putParcelableArrayList(SAVED_TRAILERS, savedTrailers);
        outState.putParcelableArrayList(SAVED_REVIEWS, savedReviews);
        super.onSaveInstanceState(outState);
    }

    public void updateViews(ArrayList<ReviewsTrailerData> detailsUpdate){


        if (!detailsUpdate.isEmpty()) {

            int detailsSize = detailsUpdate.size();

            if (detailsUpdate.get(0).trailersNotReviews && !detailsUpdate.get(0).movieTrailerName.isEmpty()) {

                //only send data to savedTrailers if it is empty, otherwise you will be passing savedTrailers back into savedTrailers when using a saved instance which will throw an error
                if (savedTrailers.isEmpty()) {
                    for (ReviewsTrailerData addTrailers : detailsUpdate) {
                        savedTrailers.add(addTrailers);
                    }
                }

                Log.v(LOG_TAG, "This is the amount of Trailers for this movie : " + detailsSize);

                //If there are more than 3 trailers only display 3
                if (detailsSize > 3){
                    createTrailerViewOne(detailsUpdate);
                    createTrailerViewTwo(detailsUpdate);
                    createTrailerViewThree(detailsUpdate);
                }
                else {
                    switch (detailsSize) {

                        case 1:
                            createTrailerViewOne(detailsUpdate);
                            break;

                        case 2:
                            createTrailerViewOne(detailsUpdate);
                            createTrailerViewTwo(detailsUpdate);
                            break;

                        case 3:
                            createTrailerViewOne(detailsUpdate);
                            createTrailerViewTwo(detailsUpdate);
                            createTrailerViewThree(detailsUpdate);
                            break;
                    }
                }


                //onClickListener for all trailers - sends to default youtube application when clicked
                View.OnClickListener clicker = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        switch (v.getId()) {
                            case R.id.movie_youtube_textview1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerPath)));
                                break;
                            case R.id.movie_youtube_textview2:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerPath2)));
                                break;

                            case R.id.movie_youtube_textview3:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerPath3)));
                                break;
                        }
                    }
                };


                //Set all Trailers to use custom onClickListener created above
                mMovieTrailerTextview1.setOnClickListener(clicker);
                mMovieTrailerTextview2.setOnClickListener(clicker);
                mMovieTrailerTextview3.setOnClickListener(clicker);


            } else if (!detailsUpdate.get(0).trailersNotReviews && !detailsUpdate.get(0).movieReviewAuthor.isEmpty()) {

                //Same as above, only fill savedReviews if its empty otherwise when using a saved instance it will throw an error
                if (savedReviews.isEmpty()){
                for (ReviewsTrailerData passReviews : detailsUpdate) {
                    savedReviews.add(passReviews);
                }
                }

                Log.v(LOG_TAG, "This is the amount of reviews : " + detailsSize);

                if (detailsSize > 3) {
                    createReviewOne(detailsUpdate);
                    createReviewTwo(detailsUpdate);
                    createReviewThree(detailsUpdate);
                }
                else {

                    switch (detailsSize) {
                        case 1:
                            createReviewOne(detailsUpdate);
                            break;

                        case 2:
                            createReviewOne(detailsUpdate);
                            createReviewTwo(detailsUpdate);
                            break;

                        case 3:
                            createReviewOne(detailsUpdate);
                            createReviewTwo(detailsUpdate);
                            createReviewThree(detailsUpdate);
                            break;

                    }
                }


                Button.OnClickListener reviewButtonClicker = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.movie_review_readmore:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mReviewPath)));
                                break;

                            case R.id.movie_review_readmore2:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mReviewPath2)));
                                break;

                            case R.id.movie_review_readmore3:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mReviewPath3)));
                                break;

                        }

                    }
                };

                buttonReadmore.setOnClickListener(reviewButtonClicker);
                buttonReadmore2.setOnClickListener(reviewButtonClicker);
                buttonReadmore3.setOnClickListener(reviewButtonClicker);
            }
        }
        else {

        }

    }


    //All View handling is done below for Trailers and Reviews - This keeps all the logic above organized and unnecessary repetition to a minimum


    private void createReviewThree(ArrayList<ReviewsTrailerData> detailsUpdate) {
        mReviewLine2.setVisibility(View.VISIBLE);

        mReviewText3 = detailsUpdate.get(2).movieReview;
        mReviewAuthor3 = detailsUpdate.get(2).movieReviewAuthor;
        mReviewPath3 = detailsUpdate.get(2).movieReviewLink;

        mMovieReviewAuthor3.setVisibility(View.VISIBLE);
        mMovieReviewAuthor3.setText(mReviewAuthor3);

        mMovieReviewText3.setVisibility(View.VISIBLE);
        mMovieReviewText3.setText(mReviewText3);

        buttonReadmore3.setVisibility(View.VISIBLE);
    }

    private void createReviewTwo(ArrayList<ReviewsTrailerData> detailsUpdate) {
        mReviewLine1.setVisibility(View.VISIBLE);

        mReviewText2 = detailsUpdate.get(1).movieReview;
        mReviewAuthor2 = detailsUpdate.get(1).movieReviewAuthor;
        mReviewPath2 = detailsUpdate.get(1).movieReviewLink;

        mMovieReviewAuthor2.setVisibility(View.VISIBLE);
        mMovieReviewAuthor2.setText(mReviewAuthor2);

        mMovieReviewText2.setVisibility(View.VISIBLE);
        mMovieReviewText2.setText(mReviewText2);

        buttonReadmore2.setVisibility(View.VISIBLE);
    }

    private void createReviewOne(ArrayList<ReviewsTrailerData> detailsUpdate) {
        mReviewText = detailsUpdate.get(0).movieReview;
        mReviewAuthor = detailsUpdate.get(0).movieReviewAuthor;
        mReviewPath = detailsUpdate.get(0).movieReviewLink;

        mMovieReviewAuthor.setVisibility(View.VISIBLE);
        mMovieReviewAuthor.setText(mReviewAuthor);

        mMovieReviewText.setText(mReviewText);
        buttonReadmore.setVisibility(View.VISIBLE);
    }

    //Create Trailer Views to display - used in the switch above depending on how many trailers are available (maximum 3)
    private void createTrailerViewThree(ArrayList<ReviewsTrailerData> detailsUpdate) {
        mTrailerLinearLayout3.setVisibility(View.VISIBLE);
        mTrailerLine2.setVisibility(View.VISIBLE);

        mTrailerPath3 = detailsUpdate.get(2).movieTrailerPath;
        mTrailerName3 = detailsUpdate.get(2).movieTrailerName;

        mMovieTrailerTextview3.setVisibility(View.VISIBLE);
        mMovieTrailerPlayIcon3.setVisibility(View.VISIBLE);

        mMovieTrailerTextview3.setText(mTrailerName3);
        mMovieTrailerPlayIcon3.setImageResource(R.drawable.ic_play_arrow_white_18dp);

        mMovieTrailerTextview3.isClickable();
    }

    private void createTrailerViewTwo(ArrayList<ReviewsTrailerData> detailsUpdate) {
        mTrailerLine1.setVisibility(View.VISIBLE);
        mTrailerLinearLayout2.setVisibility(View.VISIBLE);

        mTrailerPath2 = detailsUpdate.get(1).movieTrailerPath;
        mTrailerName2 = detailsUpdate.get(1).movieTrailerName;

        mMovieTrailerTextview2.setVisibility(View.VISIBLE);
        mMovieTrailerPlayIcon2.setVisibility(View.VISIBLE);

        mMovieTrailerTextview2.setText(mTrailerName2);
        mMovieTrailerPlayIcon2.setImageResource(R.drawable.ic_play_arrow_white_18dp);

        mMovieTrailerTextview2.isClickable();
    }

    private void createTrailerViewOne(ArrayList<ReviewsTrailerData> detailsUpdate) {
        mTrailerPath = detailsUpdate.get(0).movieTrailerPath;
        mTrailerName = detailsUpdate.get(0).movieTrailerName;

        mMovieTrailerTextview1.setText(mTrailerName);
        mMovieTrailerPlayIcon1.setImageResource(R.drawable.ic_play_arrow_white_18dp);

        mMovieTrailerTextview1.isClickable();
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        try {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            //Set the title on the actionbar
            getActivity().setTitle(title);

            //create a 5 star system from the 10 star rating given at TMDb
            float ratingNum = Float.parseFloat(rating);
            ratingNum /= 2;
            Log.v(LOG_TAG, "ratingNum = " + ratingNum);

            //All Views set but not used yet
            //General Information passed through the arguments
            mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title_textview);
            mMovieSynopsis = (TextView) rootView.findViewById(R.id.movie_synopsis_textview);
            mMovieRelease = (TextView) rootView.findViewById(R.id.movie_release_textview);
            mMovieRatingBar = (RatingBar) rootView.findViewById(R.id.movie_ratingbar);
            backDropImageView = (ImageView) rootView.findViewById(R.id.movie_backdrop_imageview);

            //Author TextViews for Reviews
            mMovieReviewAuthor = (TextView) rootView.findViewById(R.id.movie_review_author);
            mMovieReviewAuthor2 = (TextView) rootView.findViewById(R.id.movie_review_author2);
            mMovieReviewAuthor3 = (TextView) rootView.findViewById(R.id.movie_review_author3);

            //Review Text for Reviews
            mMovieReviewText = (TextView) rootView.findViewById(R.id.movie_review_textview1);
            mMovieReviewText2 = (TextView) rootView.findViewById(R.id.movie_review_textview2);
            mMovieReviewText3 = (TextView) rootView.findViewById(R.id.movie_review_textview3);

            //Linear Layouts for Trailers ----- Used to set visibility of each trailer
            mTrailerLinearLayout2 = (LinearLayout) rootView.findViewById(R.id.trailer2_linear_layout);
            mTrailerLinearLayout3 = (LinearLayout) rootView.findViewById(R.id.trailer3_linear_layout);

            //Textviews for trailer views
            mMovieTrailerTextview1 = (TextView) rootView.findViewById(R.id.movie_youtube_textview1);
            mMovieTrailerTextview2 = (TextView) rootView.findViewById(R.id.movie_youtube_textview2);
            mMovieTrailerTextview3 = (TextView) rootView.findViewById(R.id.movie_youtube_textview3);

            //Play icons for trailer views
            mMovieTrailerPlayIcon1 = (ImageView) rootView.findViewById(R.id.movie_youtube_play_icon);
            mMovieTrailerPlayIcon2 = (ImageView) rootView.findViewById(R.id.movie_youtube_play_icon2);
            mMovieTrailerPlayIcon3 = (ImageView) rootView.findViewById(R.id.movie_youtube_play_icon3);

            //Lines used for formatting ------ Used to set visibility of each line
            mTrailerLine1 = rootView.findViewById(R.id.trailer1_line);
            mTrailerLine2 = rootView.findViewById(R.id.trailer2_line);
            mReviewLine1 = rootView.findViewById(R.id.review1_line);
            mReviewLine2 = rootView.findViewById(R.id.review2_line);

            //Button below the Review to read more
            buttonReadmore = (Button) rootView.findViewById(R.id.movie_review_readmore);
            buttonReadmore2 = (Button) rootView.findViewById(R.id.movie_review_readmore2);
            buttonReadmore3 = (Button) rootView.findViewById(R.id.movie_review_readmore3);

            //set values for all non-image views
            mMovieTitle.setText(title);
            mMovieSynopsis.setText(synopsis);
            mMovieRelease.setText(release);

            //Didnt use the below code because its not compatible on all devices
//            Drawable drawable = mMovieRatingBar.getProgressDrawable();
//            drawable.setColorFilter(Color.parseColor("#BF360C"), PorterDuff.Mode.SRC_ATOP);

            //Set the rating on the Rating Bar
            mMovieRatingBar.setRating(ratingNum);

            //backDropImageView and posterImageView defined
            Picasso.with(getActivity()).load(backdrop).placeholder(R.drawable.transitionbackdrop).error(R.drawable.transitionbackdroperror).fit().into(backDropImageView);

            //Load Poster for phones but not Tablets
            if (!MainActivity.twoPaneView) {
                posterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_imageview);
                Picasso.with(getActivity()).load(poster).placeholder(R.drawable.posterplaceholder).error(R.drawable.postererror).fit().into(posterImageView);
            }


            final RealmResults<Favorite> favResults = myRealm.where(Favorite.class).equalTo("title", title).findAll();

            //All buttonFavorite items below, if movie isnt favorited button says "favorite" and can add to db, otherwise reverse
            buttonFavorite = (Button) rootView.findViewById(R.id.action_favorites);

            if (favResults.isEmpty()){
                buttonFavorite.setText("FAVORITE");
            } else if (!favResults.isEmpty()){
                buttonFavorite.setText("UNFAVORITE");
            }


            buttonFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "Favorite Button pressed on Detail View");

//                    myRealm.beginTransaction();
                    if (favResults.isEmpty()) {
                        myRealm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Favorite favorite = realm.createObject(Favorite.class);
                                favorite.setUserRating(rating);
                                favorite.setSynopsis(synopsis);
                                favorite.setReleaseDate(release);
                                favorite.setPosterPath(poster);
                                favorite.setBackDrop(backdrop);
                                favorite.setMovieId(movieId);
                                favorite.setTitle(title);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Log.v("@@SUCCESS", "ADDED SUCCESSFULLY");
                                Toast.makeText(getContext(), "ADDED FAVORITE", Toast.LENGTH_SHORT).show();
                                buttonFavorite.setText("UNFAVORITE");
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Log.v("@@FAIL", "ERROR IN ADDING TO DB");
                            }
                        });
                    } else if(!favResults.isEmpty()){
                        Toast.makeText(getContext(), "DELETED FAVORITE", Toast.LENGTH_SHORT).show();
                        buttonFavorite.setText("FAVORITE");

                        myRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                favResults.deleteAllFromRealm();
                            }
                        });

                    }

//                    myRealm.commitTransaction();


//                    final RealmResults<Favorite> favResults =
//                            myRealm.where(Favorite.class).findAll();

//                    for(Favorite c:favResults) {
//                        Log.d("favResults", c.getTitle());
//                    }

//                    if (Favorite.find(Favorite.class, "title = ?", title).isEmpty()) {
//                        Favorite favorite = new Favorite(title, synopsis, release, rating, poster, backdrop, movieId);
//                        favorite.save();
//                        Toast toast = Toast.makeText(getActivity(), "Favorite Saved", Toast.LENGTH_SHORT);
//                        toast.show();
//                        buttonFavorite.setText("UNFAVORITE");
//                    } else if (!Favorite.find(Favorite.class, "title = ?", title).isEmpty()) {
//                        Favorite.deleteAll(Favorite.class, "title = ?", title);
//                        Toast toast = Toast.makeText(getActivity(), "Favorite Removed", Toast.LENGTH_SHORT);
//                        toast.show();
//                        buttonFavorite.setText("FAVORITE");
//                    }
                }
            });

            //If we have a saved state, run updateViews with the saved data as passed in from onCreate
            if (savedInstanceState != null) {
                updateViews(savedTrailers);
                updateViews(savedReviews);
            }

            return rootView;
        } catch (Exception e) {
            Log.v(LOG_TAG, " This is the onCreateView - STILL NO DATA FROM THE onCreate method");
            return null;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        myRealm.close();
        Log.v("@#$@#$", "CLOSED THE DETAILFRAGMENT");
    }


    //Async task below handles fetching trailers and reviews data as well as passing that data into an updateView method

    public class FetchTrailersTask extends AsyncTask<String, Void, ReviewsTrailerData[]> {
        private ProgressDialog dialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        private ReviewsTrailerData[] getReviewsData(String trailerJsonString, boolean trailersNotReviews) throws JSONException {

            final String JSON_RESULTS = "results";
            final String JSON_AUTHOR = "author";
            final String JSON_CONTENT = "content";
            final String JSON_URL = "url";
            final String JSON_NAME = "name";
            final String JSON_KEY = "key";


            JSONObject detailsJSON = new JSONObject(trailerJsonString);
            JSONArray detailsArray = detailsJSON.getJSONArray(JSON_RESULTS);

            ReviewsTrailerData[] resultDetailsInfo = new ReviewsTrailerData[detailsArray.length()];


                //if passing Reviews data then fill the review variables with 0's
                if (trailersNotReviews) {
                    for (int i=0; i < detailsArray.length(); i++) {
                        String name;
                        String key;

                        //Get the JSON object for the movie
                        JSONObject movieInformation = detailsArray.getJSONObject(i);

                        name = movieInformation.getString(JSON_NAME);
                        key = movieInformation.getString(JSON_KEY);

                        //Add new MovieData Object into resultMovieInfo list
                        resultDetailsInfo[i] = new ReviewsTrailerData("0", "0", "0", name, key, true);
                    }
                }
                //if passing Trailer data then fill the review variables with 0's
                else if (!trailersNotReviews) {
                    for (int i=0; i < detailsArray.length(); i++) {
                        String author;
                        String content;
                        String reviewURL;

                        //Get the JSON object for the movie
                        JSONObject movieInformation = detailsArray.getJSONObject(i);

                        //get all information to pass into the MovieData Object
                        author = movieInformation.getString(JSON_AUTHOR);
                        content = movieInformation.getString(JSON_CONTENT);
                        reviewURL = movieInformation.getString(JSON_URL);

                        //Add new MovieData Object into resultMovieInfo list
                        resultDetailsInfo[i] = new ReviewsTrailerData(content, author, reviewURL, "0", "0", false);
                    }
                }
            return resultDetailsInfo;

        }

        @Override
        protected ReviewsTrailerData[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String detailsJsonStr = null;
            String trailerReview;
            int trailerReviewInt;


                trailerReview = params[0];
                trailerReviewInt = Integer.parseInt(trailerReview);
                Log.v(LOG_TAG, "The Params for DoInBackground are as follows : " + trailerReviewInt);

                try {

                    final String DB_TRAILER_REVIEWS_BASE_URL = "http://api.themoviedb.org/3/movie/";
                    final String REVIEWS_SEARCH = "/reviews?";
                    final String TRAILERS_SEARCH = "/videos?";
                    final String API_ADDON = "&api_key=";
                    final String FINAL_DB_URL;

                    final String API_KEY = BuildConfig.API_KEY;

                    switch (trailerReviewInt) {
                        case 1:
                            FINAL_DB_URL = DB_TRAILER_REVIEWS_BASE_URL + movieId + TRAILERS_SEARCH + API_ADDON + API_KEY;
                            break;
                        case 2:
                            FINAL_DB_URL = DB_TRAILER_REVIEWS_BASE_URL + movieId + REVIEWS_SEARCH + API_ADDON + API_KEY;
                            break;
                        default:
                            FINAL_DB_URL = "Broken Link";
                            Log.e(LOG_TAG, "The Link to the Review or Trailer is broken, check the switch statement.");
                            break;
                    }

                    URL url = new URL(FINAL_DB_URL);

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

                    detailsJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "JSON string : " + detailsJsonStr);


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
                    switch (trailerReviewInt) {
                        case 1:
                            Log.v(LOG_TAG, "try method for trailer with getReviewsData ran");
                            return getReviewsData(detailsJsonStr, true);
                        case 2:
                            Log.v(LOG_TAG, "try method for reviews with getReviewsData ran");
                            return getReviewsData(detailsJsonStr, false);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }


                return null;

        }


        @Override
        protected void onPostExecute(ReviewsTrailerData[] result) {

            if (dialog.isShowing()){
                dialog.dismiss();
            }

            if (result != null) {
                    ArrayList<ReviewsTrailerData> mRAuthor = new ArrayList<>();
                    try {
                        for (ReviewsTrailerData dd : result) {
                            mRAuthor.add(dd);
                        }

                        updateViews(mRAuthor);
                    }
                    catch (Exception e) {
                        updateViews(mRAuthor);
                        Log.i(LOG_TAG, "Either there are no reviews or the data wasnt passed correctly");
                    }

                }


        }


    }


}
