package com.example.zac.pickflick;

import com.orm.SugarRecord;

/**
 * Created by Zac on 11/5/15.
 */
public class Favorite extends SugarRecord<Favorite> {

    String title;
    String synopsis;
    String releaseDate;
    String userRating;
    String posterPath;
    String backDrop;

    public Favorite(){
    }

    public Favorite(String title, String synopsis, String releaseDate, String userRating, String posterPath, String backDrop){
        this.title = title;
        this.synopsis = synopsis;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.posterPath = posterPath;
        this.backDrop = backDrop;
    }

}
