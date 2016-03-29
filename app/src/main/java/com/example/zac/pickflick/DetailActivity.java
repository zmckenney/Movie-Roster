package com.example.zac.pickflick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Zac on 2/4/16.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_INTENT_PASS = "DIP";

    public final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
