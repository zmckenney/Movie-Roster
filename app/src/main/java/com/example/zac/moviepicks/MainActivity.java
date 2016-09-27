package com.example.zac.moviepicks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public static boolean twoPaneView = false;
    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String DETAILFRAGMENT_DATA = "DFDATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());


        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        Realm.setDefaultConfiguration(realmConfig);

        setContentView(R.layout.activity_main);

        //Is this a 600dp or greater device?
        if (findViewById(R.id.movie_detail_container) != null) {

            //set as a two pane device
            twoPaneView = true;


            //if there isnt any data yet, create a fragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

            }
        else {
            twoPaneView = false;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /**
            if (id == R.id.action_settings) {
            Log.v(LOG_TAG, "Settings working on main screen");
            return true;
        }
         */



        return super.onOptionsItemSelected(item);
    }

//    public void noFavoritesSavedToast(){
//        Toast toast = Toast.makeText(getApplicationContext(), "You have no favorites saved", Toast.LENGTH_SHORT);
//        toast.show();
//    }
}
