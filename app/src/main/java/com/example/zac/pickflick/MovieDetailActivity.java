package com.example.zac.pickflick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity  {

    public final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private Button buttonFavorite;


    private String[] mMovieInfo;
    String title;
    String poster;
    String synopsis;
    String release;
    String rating;
    String backdrop;
    String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Pull all the data from the Intent created by MainFragment
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mMovieInfo = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            title = mMovieInfo[0];
            poster = mMovieInfo[1];
            synopsis = mMovieInfo[2];
            release = mMovieInfo[3];
            rating = mMovieInfo[4];
            backdrop = mMovieInfo[5];
            movieId = mMovieInfo[6];


        //Set the title on the actionbar
        this.setTitle(title);

        //create a 5 star system from the 10 star rating given at TMDb
        float ratingNum = Float.parseFloat(rating);
        ratingNum /= 2;
        Log.v(LOG_TAG, "ratingNum = " + ratingNum);

        ((TextView) findViewById(R.id.movie_title_textview)).setText(title);
        ((TextView) findViewById(R.id.movie_synopsis_textview)).setText(synopsis);
        ((TextView) findViewById(R.id.movie_release_textview)).setText(release);
        ((RatingBar) findViewById(R.id.movie_ratingbar)).setRating(ratingNum);


        ImageView backDropImageView = (ImageView) findViewById(R.id.movie_backdrop_imageview);
        ImageView posterImageView = (ImageView) findViewById(R.id.movie_poster_imageview);

        Picasso.with(getApplicationContext()).load(backdrop).placeholder(R.drawable.transitionbackdrop).error(R.drawable.transitionbackdroperror).fit().into(backDropImageView);
        Picasso.with(getApplicationContext()).load(poster).placeholder(R.drawable.posterplaceholder).error(R.drawable.postererror).fit().into(posterImageView);


        buttonFavorite = (Button) findViewById(R.id.action_favorites);
        buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "Favorite Button pressed on Detail View");
                if (Favorite.find(Favorite.class, "title = ?", title).isEmpty()) {
                    Favorite favorite = new Favorite(title, synopsis, release, rating, poster, backdrop, movieId);
                    favorite.save();
                }
                else if (!Favorite.find(Favorite.class, "title = ?", title).isEmpty()){

                    Favorite.deleteAll(Favorite.class,"title = ?", title);

                }

            }
        });
    }

}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /**
         if (id == R.id.action_settings) {
         return true;
         }
         */

        return super.onOptionsItemSelected(item);
    }



}
