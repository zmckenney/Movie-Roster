package com.example.zac.pickflick;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zac on 11/7/15.
 */

public class ReviewsTrailerData implements Parcelable {

    private static final String LOG_TAG = ReviewsTrailerData.class.getSimpleName();

    String movieReview;
    String movieTrailerName;
    String movieTrailerPath;
    String movieReviewAuthor;
    String movieReviewLink;
    final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
    boolean trailersNotReviews;

    public ReviewsTrailerData(String mMovieReview, String mMovieReviewAuthor, String mMovieReviewLink, String mMovieTrailerName, String mTrailerPath, boolean mTrailersNotReviews) {
        this.movieReview = mMovieReview;
        this.movieTrailerName = mMovieTrailerName;
        this.movieTrailerPath = TRAILER_BASE_URL + mTrailerPath;
        this.movieReviewAuthor = mMovieReviewAuthor;
        this.movieReviewLink= mMovieReviewLink;
        this.trailersNotReviews = mTrailersNotReviews;
    }


    private ReviewsTrailerData(Parcel in) {
        movieReview = in.readString();
        movieTrailerName = in.readString();
        movieTrailerPath = in.readString();
        movieReviewAuthor = in.readString();
        movieReviewLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieReview);
        parcel.writeString(movieTrailerName);
        parcel.writeString(movieTrailerPath);
        parcel.writeString(movieReviewAuthor);
        parcel.writeString(movieReviewLink);
    }



    public final Parcelable.Creator<ReviewsTrailerData> CREATOR = new Parcelable.Creator<ReviewsTrailerData>() {

        @Override
        public ReviewsTrailerData createFromParcel(Parcel parcel) {
            return new ReviewsTrailerData(parcel);
        }

        @Override
        public ReviewsTrailerData[] newArray(int i) {
            return new ReviewsTrailerData[i];
        }
    };



}
