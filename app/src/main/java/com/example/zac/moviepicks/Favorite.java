package com.example.zac.moviepicks;

import io.realm.RealmObject;

/**
 * Created by Zac on 11/5/15.
 */
public class Favorite extends RealmObject{

    private String title;
    private String synopsis;
    private String releaseDate;
    private String userRating;
    private String posterPath;
    private String backDrop;
    private String movieId;

    public Favorite(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackDrop() {
        return backDrop;
    }

    public void setBackDrop(String backDrop) {
        this.backDrop = backDrop;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }


    //    public Favorite(){
//    }
//
//    public Favorite(String title, String synopsis, String releaseDate, String userRating, String posterPath, String backDrop, String movId){
//        this.title = title;
//        this.synopsis = synopsis;
//        this.releaseDate = releaseDate;
//        this.userRating = userRating;
//        this.posterPath = posterPath;
//        this.backDrop = backDrop;
//        this.movieId = movId;
//    }
//
//    @Override
//    public String toString() {
//
//        return title + "'\'" + posterPath + "'\'" + synopsis + "'\'" + releaseDate + "'\'" + userRating + "'\'" + backDrop + "'\'" + movieId;
//    }
}
