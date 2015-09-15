package com.example.zac.pickflick;

/**
 * Created by Zac on 9/13/15.
 */
public class MovieData {

    String movieTitle;
    String moviePosterAppend;
    String movieOverview;
    String movieSynopsis;
    String movieReleaseDate;
    String movieUserRating;
    String posterBaseURL = "http://image.tmdb.org/t/p/w185";
    String moviePosterPath;




    public MovieData(String mTitle, String mPath, String mSynopsis, String mRelease, String mRating ) {

        this.movieTitle = mTitle;
        this.movieSynopsis = mSynopsis;
        this.movieReleaseDate = mRelease;
        this.movieUserRating = mRating;
        this.moviePosterPath = posterBaseURL + mPath;
    }



}
