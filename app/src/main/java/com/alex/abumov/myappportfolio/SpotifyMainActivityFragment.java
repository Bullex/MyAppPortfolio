package com.alex.abumov.myappportfolio;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyMainActivityFragment extends Fragment {

    private SpotifyArtistsAdapter mForecastAdapter;
    private ListView listView;

    public SpotifyMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spotify_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.ss_main_list_view);

        List<SpotifyArtistItem> items = new ArrayList<>();
        items.add(new SpotifyArtistItem("http://i59.fastpic.ru/big/2013/1202/be/2fcf9a5990ea9b3c80000a9076e13bbe.jpg", "Timati"));
        items.add(new SpotifyArtistItem("http://www.morozrec.ru/pictures/only%20best/400/leningrad_best.jpg", "Leningrad"));
        mForecastAdapter = new SpotifyArtistsAdapter(getActivity(), R.layout.spotify_main_list_view_row, items);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String itemValue = mForecastAdapter.getItem(position);
//                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
//                detailIntent.putExtra(Intent.EXTRA_TEXT, itemValue);
//                startActivity(detailIntent);
            }
        });
        return rootView;
    }
}
