package com.alex.abumov.myappportfolio.Spotify;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alex.abumov.myappportfolio.DataParser;
import com.alex.abumov.myappportfolio.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyTrackListActivityFragment extends Fragment {

    private String mArtistId;
    private String mArtistName;
    private ListView listView;
    private SpotifyTracksAdapter mTracksAdapter;

    public SpotifyTrackListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_spotify_track_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.ss_tracks_list_view);

        List<SpotifyTrackItem> items = new ArrayList<>();
        mTracksAdapter = new SpotifyTracksAdapter(getActivity(), R.layout.spotify_main_list_view_row, items);
        listView.setAdapter(mTracksAdapter);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            mArtistName = intent.getStringExtra(Intent.EXTRA_TITLE);
            actionBarSetup(mArtistName);
            GetTracksTask getTracksTask = new GetTracksTask();
            getTracksTask.execute(mArtistId);
        }
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup(String artistName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActivity().getActionBar();
            if(ab != null) {
                ab.setSubtitle(artistName);
            }
        }
    }

    private class GetTracksTask extends AsyncTask<String, Void, ArrayList<SpotifyTrackItem>> {

        protected ArrayList<SpotifyTrackItem> doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String tracksJsonStr = null;
            String country = "US";

            try {
                final String TRACKS_BASE_URL =
                        "https://api.spotify.com/v1/artists/"+params[0]+"/top-tracks?";
                final String COUNTRY_PARAM = "country";

                Uri buildUri = Uri.parse(TRACKS_BASE_URL).buildUpon()
                        .appendQueryParameter(COUNTRY_PARAM, country)
                        .build();

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                tracksJsonStr = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                DataParser dataParser = new DataParser();
                return dataParser.getTracksDataFromJson(tracksJsonStr);
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }



        protected void onPostExecute(ArrayList<SpotifyTrackItem> result) {
            if (result != null){
                mTracksAdapter.clear();
                for (SpotifyTrackItem trackItem : result){
                    mTracksAdapter.add(trackItem);
                }
            }
        }
    }
}
