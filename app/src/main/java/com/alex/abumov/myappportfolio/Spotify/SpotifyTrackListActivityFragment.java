package com.alex.abumov.myappportfolio.Spotify;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.abumov.myappportfolio.DataParser;
import com.alex.abumov.myappportfolio.Network;
import com.alex.abumov.myappportfolio.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyTrackListActivityFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;



    private String mArtistId = "";
    private String mArtistName = "";
    private TextView textView;
    static public ArrayList<SpotifyTrackItem> items;
    private SpotifyTracksAdapter mTracksAdapter;

    public SpotifyTrackListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_spotify_track_list, container, false);
        textView = (TextView) rootView.findViewById(R.id.ss_track_list_empty_text);

        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            items = new ArrayList<>();
        } else {
            items = savedInstanceState.getParcelableArrayList("key");
        }
        mTracksAdapter = new SpotifyTracksAdapter(getActivity(), R.layout.spotify_main_list_view_row, items);
        setListAdapter(mTracksAdapter);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            mArtistName = intent.getStringExtra(Intent.EXTRA_TITLE);
        }else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mArtistId = getArguments().getString(Intent.EXTRA_TEXT, "");
                mArtistName = getArguments().getString(Intent.EXTRA_TITLE, "");
            }
        }
        if (!mArtistId.isEmpty() && !mArtistName.isEmpty()){
            actionBarSetup(mArtistName);
            if (Network.isNetworkAvailable(getActivity())){
                GetTracksTask getTracksTask = new GetTracksTask();
                getTracksTask.execute(mArtistId);
            }else{
                Toast toast = Toast.makeText(getActivity(), getString(R.string.not_network), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", items);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
//
//        // Notify the active callbacks interface (the activity, if the
//        // fragment is attached to one) that an item has been selected.
//        mCallbacks.onItemSelected(items.get(position));
        SpotifyTrackItem trackItem = items.get(position);
        if (SpotifyMainActivity.mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(SpotifyPlayerActivityFragment.TRACK_INDEX,
                    position);
            arguments.putString(SpotifyPlayerActivityFragment.ARTIST_ID,
                    trackItem.getArtistId());
            arguments.putString(SpotifyPlayerActivityFragment.ARTIST_NAME,
                    trackItem.getArtist());
            arguments.putString(SpotifyPlayerActivityFragment.ALBUM_NAME,
                    trackItem.getAlbum());
            arguments.putString(SpotifyPlayerActivityFragment.TRACK_NAME,
                    trackItem.getName());
            arguments.putString(SpotifyPlayerActivityFragment.TRACK_THUMBNAIL_URL,
                    trackItem.getAlbumImgUrl());
            arguments.putString(SpotifyPlayerActivityFragment.TRACK_PREVIEW_URL,
                    trackItem.getPreviewUrl());
            SpotifyPlayerActivityFragment fragment = new SpotifyPlayerActivityFragment();
            fragment.setArguments(arguments);
            fragment.show(getFragmentManager(), fragment.getTag());
        } else {
            Intent detailIntent = new Intent(getActivity(), SpotifyPlayerActivity.class);
            detailIntent.putExtra(SpotifyPlayerActivityFragment.TRACK_INDEX,
                    position);
            detailIntent.putExtra(SpotifyPlayerActivityFragment.ARTIST_ID,
                    trackItem.getArtistId());
            detailIntent.putExtra(SpotifyPlayerActivityFragment.ARTIST_NAME,
                    trackItem.getArtist());
            detailIntent.putExtra(SpotifyPlayerActivityFragment.ALBUM_NAME,
                    trackItem.getAlbum());
            detailIntent.putExtra(SpotifyPlayerActivityFragment.TRACK_NAME,
                    trackItem.getName());
            detailIntent.putExtra(SpotifyPlayerActivityFragment.TRACK_THUMBNAIL_URL,
                    trackItem.getAlbumImgUrl());
            detailIntent.putExtra(SpotifyPlayerActivityFragment.TRACK_PREVIEW_URL,
                    trackItem.getPreviewUrl());
            startActivity(detailIntent);
        }
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
                if (mTracksAdapter.getCount() == 0) {
                    getListView().setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    getListView().setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }
            }
        }
    }
}
