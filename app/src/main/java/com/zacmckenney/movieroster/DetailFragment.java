package com.zacmckenney.movieroster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.zacmckenney.movieroster.data.Favorite;

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

public class DetailFragment extends Fragment {

    public final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String SAVED_TRAILERS = "SAVEDTRAILERS";
    static final String SAVED_REVIEWS = "SAVEDREVIEWS";

    private View rootView;
    private String[] mMovieData;

    //Strings to be passed in via arguments
    String title;
    String poster;
    String synopsis;
    String release;
    String rating;
    String backdrop;
    String movieId;

    private Button buttonFavorite;

    private ArrayList<ReviewsTrailerData> savedReviews = new ArrayList<>();
    private ArrayList<ReviewsTrailerData> savedTrailers = new ArrayList<>();

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

                //Fetch Data for the reviews and trailers only if there isnt a saved instance
                if (savedInstanceState == null) {
                    new FetchTrailersAndReviews().execute("1");
                    new FetchTrailersAndReviews().execute("2");
                }

                //Only pass data into savedTrailers and savedReviews, onCreateView needs to run first to create all the views before we can run updateViews
                else {
                    savedTrailers = savedInstanceState.getParcelableArrayList(SAVED_TRAILERS);
                    savedReviews = savedInstanceState.getParcelableArrayList(SAVED_REVIEWS);
                }

                //This is OK to catch when using a tablet, as soon as an item is selected it will properly run the above code
            } catch (Exception e) {
                Log.v(LOG_TAG, "exception e thrown in onCreate : " + e);
            }
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        //Save the reviews and trailer data
        outState.putParcelableArrayList(SAVED_TRAILERS, savedTrailers);
        outState.putParcelableArrayList(SAVED_REVIEWS, savedReviews);

        super.onSaveInstanceState(outState);
    }

    public int dipToPixel(int dimension){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(dimension), getResources().getDisplayMetrics());
    }
//    public float sipToFloat(int dimension){
//        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(dimension), getResources().getDisplayMetrics());
//    }

    public void updateViews(final ArrayList<ReviewsTrailerData> detailsUpdate){
        if (!detailsUpdate.isEmpty()) {
            int detailsSize = detailsUpdate.size();
            if (detailsUpdate.get(0).trailersNotReviews && !detailsUpdate.get(0).movieTrailerName.isEmpty()) {

                TextView emptyView = (TextView) rootView.findViewById(R.id.empty_trailer);
                emptyView.setVisibility(View.GONE);

                //only send data to savedTrailers if it is empty, otherwise you will be passing savedTrailers back into savedTrailers when using a saved instance which will throw an error
                if (savedTrailers.isEmpty()) {
                    for (ReviewsTrailerData addTrailers : detailsUpdate) {
                        savedTrailers.add(addTrailers);
                    }
                }

                //Parent LinearLayout that is Vertical
                LinearLayout trailersLLParent = (LinearLayout) rootView.findViewById(R.id.trailers_linear_layout);

                //trailer padding
                int trailer_padding_pix = dipToPixel(R.dimen.trailer_padding);

                //create parameters for the trailers linear layouts
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int trailerMarginVertical = dipToPixel(R.dimen.trailer_margin_vertical);
                int trailerMarginHorizontal = dipToPixel(R.dimen.trailer_margin_vertical);
                layoutParams.setMargins(trailerMarginHorizontal, trailerMarginVertical, trailerMarginHorizontal, trailerMarginVertical);

                //text sizes for trailers and reviews
                float trailerTextSize = getResources().getDimension(R.dimen.main_text_size);

                for (int i = 0; i < detailsSize && i <= 10; i++){
                    //Horizontal LinearLayout for each trailer
                    LinearLayout trailersLL = new LinearLayout(rootView.getContext());
                    trailersLL.setLayoutParams(layoutParams);
                    trailersLL.setOrientation(LinearLayout.HORIZONTAL);
                    trailersLL.setBackgroundColor(getResources().getColor(R.color.highlight_background_color));
                    trailersLL.setGravity(Gravity.CENTER_VERTICAL);

                    //White play button
                    ImageView iv = new ImageView(rootView.getContext());
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_18dp));
                    trailersLL.addView(iv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    //Trailer name
                    TextView tv = new TextView(rootView.getContext());
                    tv.setPadding(0, trailer_padding_pix , 0, trailer_padding_pix);
                    tv.setTextColor(getResources().getColor(R.color.detail_title_text));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, trailerTextSize);
                    tv.setText(detailsUpdate.get(i).movieTrailerName);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    trailersLL.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    final int position = i;
                    trailersLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(detailsUpdate.get(position).movieTrailerPath)));
                        }
                    });
                    trailersLLParent.addView(trailersLL);
                }



            } else if (!detailsUpdate.get(0).trailersNotReviews && !detailsUpdate.get(0).movieReviewAuthor.isEmpty()) {

                TextView emptyView = (TextView) rootView.findViewById(R.id.empty_review);
                emptyView.setVisibility(View.GONE);

                if (savedReviews.isEmpty()){
                for (ReviewsTrailerData passReviews : detailsUpdate) {
                    savedReviews.add(passReviews);
                }
                }

                //Parent LinearLayout that is Vertical
                LinearLayout reviewsLLParent = (LinearLayout) rootView.findViewById(R.id.reviews_linear_layout);

                //author padding
                int author_padding = dipToPixel(R.dimen.author_padding);

                //create parameters for the trailers linear layouts
                LinearLayout.LayoutParams authorLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int reviewMarginVertical = dipToPixel(R.dimen.review_margin_vertical);
                int reviewMarginHorizontal = dipToPixel(R.dimen.review_margin_horizontal);
                authorLayoutParams.setMargins(reviewMarginHorizontal, reviewMarginVertical, reviewMarginHorizontal, reviewMarginVertical);

                float textSize = getResources().getDimension(R.dimen.main_text_size);

                for (int i = 0; i < detailsSize && i <= 5; i++) {
                    //Horizontal LinearLayout for each trailer
                    LinearLayout reviewsLL = new LinearLayout(rootView.getContext());
                    reviewsLL.setLayoutParams(authorLayoutParams);
                    reviewsLL.setOrientation(LinearLayout.VERTICAL);

                    //Author header
                    TextView authorTV = new TextView(rootView.getContext());
                    authorTV.setPadding(author_padding, author_padding, author_padding, author_padding);
                    authorTV.setTextColor(getResources().getColor(R.color.detail_title_text));
                    authorTV.setBackgroundColor(getResources().getColor(R.color.highlight_background_color));
                    authorTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    authorTV.setText(detailsUpdate.get(i).movieReviewAuthor);
                    reviewsLL.addView(authorTV, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    //Review
                    TextView reviewTV = new TextView(rootView.getContext());
                    reviewTV.setPadding(author_padding, author_padding, author_padding, author_padding);
                    reviewTV.setTextColor(getResources().getColor(R.color.detail_main_text));
                    reviewTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    reviewTV.setText(detailsUpdate.get(i).movieReview);
                    reviewsLL.addView(reviewTV);

                    //set the entire layout to be clickable
                    final int position = i;
                    reviewsLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(detailsUpdate.get(position).movieReviewLink)));
                        }
                    });

                    reviewsLLParent.addView(reviewsLL);

                }


            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        try {
            rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            //Set the title on the actionbar
//            getActivity().setTitle(title);

            //create a 5 star system from the 10 star rating given at TMDb
            float ratingNum = Float.parseFloat(rating);
            ratingNum /= 2;
            Log.v(LOG_TAG, "ratingNum = " + ratingNum);

            //All Views set but not used yet
            //General Information passed through the arguments
            TextView mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title_textview);
            TextView mMovieSynopsis = (TextView) rootView.findViewById(R.id.movie_synopsis_textview);
            TextView mMovieRelease = (TextView) rootView.findViewById(R.id.movie_release_textview);
            RatingBar mMovieRatingBar = (RatingBar) rootView.findViewById(R.id.movie_ratingbar);
            ImageView backDropImageView = (ImageView) rootView.findViewById(R.id.movie_backdrop_imageview);

            //set values for all non-image views
            mMovieTitle.setText(title);
            mMovieSynopsis.setText(synopsis);
            mMovieRelease.setText(release);

            //Set the rating on the Rating Bar
            mMovieRatingBar.setRating(ratingNum);

            //backDropImageView and posterImageView defined
            Picasso.with(rootView.getContext()).load(backdrop).placeholder(R.drawable.transitionbackdrop).error(R.drawable.transitionbackdroperror).fit().into(backDropImageView);

            //Load Poster for phones but not Tablets
            if (!MainActivity.twoPaneView) {
                ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_poster_imageview);
                Picasso.with(rootView.getContext()).load(poster).placeholder(R.drawable.posterplaceholder).error(R.drawable.postererror).fit().into(posterImageView);
            }


            final RealmResults<Favorite> favResults = myRealm.where(Favorite.class).equalTo("title", title).findAll();

            //All buttonFavorite items below, if movie isnt favorited button says "favorite" and can add to db, otherwise reverse
            buttonFavorite = (Button) rootView.findViewById(R.id.action_favorites);

            if (favResults.isEmpty()){
                buttonFavorite.setText(getString(R.string.button_favorite));
            } else if (!favResults.isEmpty()){
                buttonFavorite.setText(getString(R.string.button_unfavorite));
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
                                buttonFavorite.setText(getString(R.string.button_unfavorite));
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Log.v("@@FAIL", "ERROR IN ADDING TO DB");
                            }
                        });
                    } else if(!favResults.isEmpty()){
                        Toast.makeText(getContext(), "DELETED FAVORITE", Toast.LENGTH_SHORT).show();
                        buttonFavorite.setText(getString(R.string.button_favorite));

                        myRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                favResults.deleteAllFromRealm();
                            }
                        });

                    }
                }
            });

            Log.v(LOG_TAG, "WE MADE IT TO THE BOTTOM OF THE ONCREATEVIEW");
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
    }




    //Async task below handles fetching trailers and reviews data as well as passing that data into an updateView method
    public class FetchTrailersAndReviews extends AsyncTask<String, Void, ReviewsTrailerData[]> {
        private ProgressDialog dialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ReviewsTrailerData[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String detailsJsonStr = null;
            String paramString;
            int trailerReviewInt;


                paramString = params[0];
                trailerReviewInt = Integer.parseInt(paramString);

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
                    StringBuilder buffer = new StringBuilder();
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
                            return getReviewsData(detailsJsonStr, true);
                        case 2:
                            return getReviewsData(detailsJsonStr, false);
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
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
        protected void onPostExecute(ReviewsTrailerData[] result) {

            if (dialog.isShowing()){
                dialog.dismiss();
            }

            if (result != null) {
                    ArrayList<ReviewsTrailerData> reviewsTrailersResults = new ArrayList<>();
                    try {
                        for (ReviewsTrailerData dd : result) {
                            reviewsTrailersResults.add(dd);
                        }
                        updateViews(reviewsTrailersResults);
                    }
                    catch (Exception e) {
                        Log.v(LOG_TAG, "Failed to add the author and trailer data : " + e);
                    }
                }
        }
    }
}
