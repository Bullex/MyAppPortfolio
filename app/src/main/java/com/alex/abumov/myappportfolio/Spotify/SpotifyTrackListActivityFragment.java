package com.alex.abumov.myappportfolio.Spotify;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.abumov.myappportfolio.DataParser;
import com.alex.abumov.myappportfolio.Network;
import com.alex.abumov.myappportfolio.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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


    // HTTP Request
    private String country = "US";
    private String track_base_url;

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
                getTracks();
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
            arguments.putString(SpotifyPlayerActivityFragment.TRACK_ID,
                    trackItem.getId());
            SpotifyPlayerActivityFragment fragment = new SpotifyPlayerActivityFragment();
            fragment.setArguments(arguments);
            fragment.show(getFragmentManager(), fragment.getTag());
        } else {
            Intent detailIntent = new Intent(getActivity(), SpotifyPlayerActivity.class);
            detailIntent.putExtra(SpotifyPlayerActivityFragment.TRACK_INDEX,
                    position);
            detailIntent.putExtra(SpotifyPlayerActivityFragment.ARTIST_ID,
                    trackItem.getArtistId());
            detailIntent.putExtra(SpotifyPlayerActivityFragment.TRACK_ID,
                    trackItem.getId());
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


    private void getTracks(){
        track_base_url = "https://api.spotify.com/v1/artists/"+mArtistId+"/top-tracks?country="+country;
        Ion.with(getActivity())
            .load(track_base_url)
            .asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if (e != null) {
                        e.printStackTrace();
                    }else{
                        if (result != null) {
                            DataParser dataParser = new DataParser();
                            try {
                                ArrayList<SpotifyTrackItem> resultArray = dataParser.getTracksDataFromJson(getActivity(), result);
                                if (resultArray != null) {
                                    mTracksAdapter.clear();
                                    for (SpotifyTrackItem trackItem : resultArray) {
                                        mTracksAdapter.add(trackItem);
                                    }
                                    if (mTracksAdapter.getCount() == 0) {
                                        getListView().setVisibility(View.GONE);
                                        textView.setVisibility(View.VISIBLE);
                                    } else {
                                        getListView().setVisibility(View.VISIBLE);
                                        textView.setVisibility(View.GONE);
                                    }
                                }
                            }catch (JsonParseException ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });
    }
}
