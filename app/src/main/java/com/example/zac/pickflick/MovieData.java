package com.example.zac.pickflick;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zac on 9/13/15.
 */
public class MovieData implements Parcelable {

    String movieTitle;
    String movieSynopsis;
    String movieReleaseDate;
    String movieUserRating;
    String posterBaseURL = "http://image.tmdb.org/t/p/w185";
    String backdropBaseURL = "http://image.tmdb.org/t/p/w600";
    String moviePosterPath;
    String movieBackDrop;




    public MovieData(String mTitle, String mPath, String mSynopsis, String mRelease, String mRating, String mBackDrop ) {

        this.movieTitle = mTitle;
        this.movieSynopsis = mSynopsis;
        this.movieReleaseDate = mRelease;
        this.movieUserRating = mRating;
        this.movieBackDrop = backdropBaseURL + mBackDrop;
        this.moviePosterPath = posterBaseURL + mPath;
    }


    private MovieData(Parcel in) {
        movieTitle = in.readString();
        movieSynopsis = in.readString();
        movieReleaseDate = in.readString();
        movieUserRating = in.readString();
        movieBackDrop = in.readString();
        moviePosterPath = in.readString();
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
