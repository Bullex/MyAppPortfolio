package com.alex.abumov.myappportfolio.Spotify;

import android.app.Activity;
import android.app.ListFragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
public class SpotifyMainActivityFragment extends ListFragment {


    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(SpotifyArtistItem artist);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(SpotifyArtistItem artist) {
        }
    };


    private SpotifyArtistsAdapter mArtistsAdapter;
    private TextView textView;
    private EditText searchView;
    private ArrayList<SpotifyArtistItem> items;

    public SpotifyMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spotify_main, container, false);
        textView = (TextView) rootView.findViewById(R.id.ss_not_found_text);
        searchView = (EditText) rootView.findViewById(R.id.ss_search_view);


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (Network.isNetworkAvailable(getActivity())) {
                        SearchArtistsTask searchTask = new SearchArtistsTask();
                        searchTask.execute(s.toString());
                    } else {
                        Toast toast = Toast.makeText(getActivity(), getString(R.string.not_network), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    mArtistsAdapter.clear();
                }
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (v.length() > 0) {
                        if (Network.isNetworkAvailable(getActivity())){
                            SearchArtistsTask searchTask = new SearchArtistsTask();
                            searchTask.execute(v.getText().toString());
                        }else{
                            Toast toast = Toast.makeText(getActivity(), getString(R.string.not_network), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        mArtistsAdapter.clear();
                    }
                    return true;
                }
                return false;
            }
        });


        if(savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            items = new ArrayList<>();
        } else {
            items = savedInstanceState.getParcelableArrayList("key");
        }

        mArtistsAdapter = new SpotifyArtistsAdapter(getActivity(), R.layout.spotify_main_list_view_row, items);
        setListAdapter(mArtistsAdapter);
        return rootView;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(items.get(position));
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class SearchArtistsTask extends AsyncTask<String, Void, ArrayList<SpotifyArtistItem>> {

        protected ArrayList<SpotifyArtistItem> doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String searchJsonStr = null;
            String type = "artist";

            try {
                final String SEARCH_BASE_URL =
                        "https://api.spotify.com/v1/search?";
                final String QUERY_PARAM = "q";
                final String TYPE_PARAM = "type";

                Uri buildUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(TYPE_PARAM, type)
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
                searchJsonStr = buffer.toString();
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
                return dataParser.getSearchDataFromJson(searchJsonStr);
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }



        protected void onPostExecute(ArrayList<SpotifyArtistItem> result) {
            if (result != null){
                mArtistsAdapter.clear();
                for (SpotifyArtistItem artistItem : result){
                    mArtistsAdapter.add(artistItem);
                }
                if (mArtistsAdapter.getCount() == 0) {
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
