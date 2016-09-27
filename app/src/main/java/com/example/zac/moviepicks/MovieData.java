package com.example.zac.moviepicks;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zac on 9/13/15.
 */
public class MovieData implements Parcelable {

    private static final String LOG_TAG = MovieData.class.getSimpleName();

    String movieTitle;
    String movieSynopsis;
    String movieReleaseDate;
    String movieUserRating;
    String moviePosterPath;
    String movieBackDrop;
    String movieId;
    String standOrFav;
    int standOrFavInt;
    final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    final String BACKDROP_BASE_URL = "http://image.tmdb.org/t/p/w600";

    //mStandOrFav - Stand = 0, Fav = 1

    public MovieData(String mTitle, String mPath, String mSynopsis, String mRelease, String mRating, String mBackDrop, String mId, String mStandOrFav) {

        this.movieTitle = mTitle;
        this.movieSynopsis = mSynopsis;
        this.movieReleaseDate = mRelease;
        this.movieUserRating = mRating;
        this.movieBackDrop = mBackDrop;
        this.moviePosterPath = mPath;
        this.movieId = mId;
        this.standOrFav = mStandOrFav;
        PosterPaths();

    }

    private void PosterPaths(){
        standOrFavInt = Integer.parseInt(standOrFav);
        if (standOrFavInt == 0) {
            this.movieBackDrop = BACKDROP_BASE_URL + movieBackDrop;
            this.moviePosterPath = POSTER_BASE_URL + moviePosterPath;
        }
    }


    private MovieData(Parcel in) {
        movieTitle = in.readString();
        movieSynopsis = in.readString();
        movieReleaseDate = in.readString();
        movieUserRating = in.readString();
        movieBackDrop = in.readString();
        moviePosterPath = in.readString();
        movieId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieTitle);
        parcel.writeString(movieSynopsis);
        parcel.writeString(movieReleaseDate);
        parcel.writeString(movieUserRating);
        parcel.writeString(movieBackDrop);
        parcel.writeString(moviePosterPath);
        parcel.writeString(movieId);
    }


    public final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {

        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int i) {
            return new MovieData[i];
        }



    };



}
