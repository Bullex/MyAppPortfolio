package com.alex.abumov.myappportfolio.Spotify;

import android.app.Fragment;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
public class SpotifyMainActivityFragment extends Fragment {

    private SpotifyArtistsAdapter mArtistsAdapter;
    private ListView listView;
    private TextView textView;
    private EditText searchView;

    public SpotifyMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spotify_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.ss_main_list_view);
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
                if(s.length() > 2){
                    SearchArtistsTask searchTask = new SearchArtistsTask();
                    searchTask.execute(s.toString());
                }else{
                    mArtistsAdapter.clear();
                }
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(v.length() > 2){
                        SearchArtistsTask searchTask = new SearchArtistsTask();
                        searchTask.execute(v.getText().toString());
                    }else{
                        mArtistsAdapter.clear();
                    }
                    return true;
                }
                return false;
            }
        });

        List<SpotifyArtistItem> items = new ArrayList<>();
        mArtistsAdapter = new SpotifyArtistsAdapter(getActivity(), R.layout.spotify_main_list_view_row, items);
        listView.setAdapter(mArtistsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyArtistItem itemValue = mArtistsAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), SpotifyTrackListActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, itemValue.getId());
                detailIntent.putExtra(Intent.EXTRA_TITLE, itemValue.getName());
                startActivity(detailIntent);
            }
        });
        return rootView;
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
                    listView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    listView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }
            }
        }
    }
}