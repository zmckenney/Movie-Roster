package com.example.zac.movieroster.data;

import io.realm.RealmObject;

/**
 * Created by Zac on 9/28/16.
 */
public class Movies extends RealmObject{
    public Movies(){}

    private String movieTitle;
    private String movieSynopsis;
    private String movieReleaseDate;
    private String movieUserRating;
    private String moviePosterPath;
    private String movieBackDrop;
    private String movieId;
    private String category;
    private int dayOfYear;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public String getMovieUserRating() {
        return movieUserRating;
    }

    public void setMovieUserRating(String movieUserRating) {
        this.movieUserRating = movieUserRating;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public void setMoviePosterPath(String moviePosterPath) {
        this.moviePosterPath = moviePosterPath;
    }

    public String getMovieBackDrop() {
        return movieBackDrop;
    }

    public void setMovieBackDrop(String movieBackDrop) {
        this.movieBackDrop = movieBackDrop;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }
}
