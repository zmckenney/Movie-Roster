package com.example.zac.pickflick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Zac on 11/7/15.
 */
public class TrailerAdapter extends ArrayAdapter<TrailerData> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private ArrayList<TrailerData> trailerData = new ArrayList<TrailerData>();


    //public void addPosterData(String movieDataFromFragment){
    //posterData.add(new MovieData("String mTitle", movieDataFromFragment, "String mOverview", "String mSynopsis", "String mRelease", "String mRating"));
    //}


    // TRY THIS CODE BUT NOT SURE IF WE NEED IT BECAUSE WE ARE ONLY GOING TO POPULATE A SINGLE TEXTVIEW WITH THIS ADAPTER
    public TrailerAdapter(Context context, ArrayList<TrailerData> trailerDatas) {
        super(context, 0, trailerDatas);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TrailerData trailerdata = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_movie_detail, parent, false);
        }

        //ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_poster);
        //Picasso.with(getContext()).load(trailerdata.moviePosterPath).placeholder(R.drawable.posterplaceholder).error(R.drawable.postererror).fit().into(imageView);

        TextView textView = (TextView) convertView.findViewById(R.id.movie_youtube_textview);
        textView.setText("First Trailer Test");
        return convertView;
    }





}
