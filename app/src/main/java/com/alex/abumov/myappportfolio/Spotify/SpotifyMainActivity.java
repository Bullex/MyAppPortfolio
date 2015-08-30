package com.alex.abumov.myappportfolio.Spotify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.alex.abumov.myappportfolio.R;


public class SpotifyMainActivity extends ActionBarActivity implements SpotifyMainActivityFragment.Callbacks {

    static public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_main);

        if (findViewById(R.id.fragment_track_list) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
//            ((SpotifyMainActivityFragment) getFragmentManager()
//                    .findFragmentById(R.id.fragment))
//                    .setActivateOnItemClick(true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, new SpotifyMainActivityFragment())
                    .commit();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_track_list, new SpotifyTrackListActivityFragment())
                    .commit();
        }else{
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment, new SpotifyMainActivityFragment())
//                    .commit();
//            SpotifyMainActivityFragment fragment = new SpotifyMainActivityFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new SpotifyMainActivityFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spotify_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(SpotifyArtistItem artist) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Intent.EXTRA_TEXT, artist.getId());
            arguments.putString(Intent.EXTRA_TITLE, artist.getName());
            SpotifyTrackListActivityFragment fragment = new SpotifyTrackListActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_track_list, fragment)
                    .commit();
        } else {
            Intent detailIntent = new Intent(getBaseContext(), SpotifyTrackListActivity.class);
            detailIntent.putExtra(Intent.EXTRA_TEXT, artist.getId());
            detailIntent.putExtra(Intent.EXTRA_TITLE, artist.getName());
            startActivity(detailIntent);
        }
    }
}
