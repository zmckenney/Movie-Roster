package com.example.zac.pickflick;

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

    private ArrayList<MovieData> posterData = new ArrayList<MovieData>();


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

        //TextView textView = (TextView) convertView.findViewById(R.id.list_item_poster);
        //textView.setText(movieData.moviePosterPath);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_poster);
        Picasso.with(getContext()).load(movieData.moviePosterPath).placeholder(R.drawable.terminator).error(R.drawable.theinternship).fit().into(imageView);

        return convertView;
    }


    // -=-==--==--=-==-=--==--=ALL CODE BELOW IS OLD, SAVED IN CASE I NEED TO REVERT BACK -=-=-==-==-=-=-
    /**

    public Context mContext;

    public PosterAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    =--==-=-=-=--=-=-=-=-=-==-=-==-=--==-ALL ABOVE WAS PREVIOUS CODE - IF THIS DOESNT WORK REVERT BACK TO IT I GUESS? ==--==-=-=-=-=-=-=--=
    */

    //TODO: set DetailAdapter in DetailFragment



}
