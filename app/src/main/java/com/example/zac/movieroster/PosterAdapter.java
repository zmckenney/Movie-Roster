package com.example.zac.movieroster;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Zac on 9/8/15.
 */

public class PosterAdapter extends ArrayAdapter<MovieData> {

    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();

    //private ArrayList<MovieData> posterData = new ArrayList<MovieData>();


    //public void addPosterData(String movieDataFromFragment){
        //posterData.add(new MovieData("String mTitle", movieDataFromFragment, "String mOverview", "String mSynopsis", "String mRelease", "String mRating"));
    //}


    // TRY THIS CODE BUT NOT SURE IF WE NEED IT BECAUSE WE ARE ONLY GOING TO POPULATE A SINGLE IMAGEVIEW WITH THIS ADAPTER
    public PosterAdapter(Activity context, ArrayList<MovieData> movieDatas) {
        super(context, 0, movieDatas);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieData movieData = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movies, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_poster);
        Picasso.with(getContext()).load(movieData.moviePosterPath).placeholder(R.drawable.posterplaceholder).error(R.drawable.postererror).fit().into(imageView);
        return convertView;
    }





}
