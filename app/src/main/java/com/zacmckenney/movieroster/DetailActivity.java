package com.zacmckenney.movieroster;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zac on 2/4/16.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_INTENT_PASS = "DIP";

    public final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

        //if we dont have a savedInstanceState then create a new fragment and use the bundled arguments
        if (savedInstanceState == null) {

        Bundle args = getIntent().getBundleExtra(DETAIL_INTENT_PASS);

        DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        }

    }
}
