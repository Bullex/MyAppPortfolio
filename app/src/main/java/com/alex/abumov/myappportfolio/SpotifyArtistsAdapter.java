package com.alex.abumov.myappportfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SpotifyArtistsAdapter extends ArrayAdapter<SpotifyArtistItem> {

    public SpotifyArtistsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SpotifyArtistsAdapter(Context context, int resource, List<SpotifyArtistItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.spotify_main_list_view_row, null);
        }

        SpotifyArtistItem p = getItem(position);

        if (p != null) {
            ImageView thumbnailView  = (ImageView) v.findViewById(R.id.ss_thumbnail);
            TextView titleView = (TextView) v.findViewById(R.id.ss_title);

            if (thumbnailView != null) {
                String url = p.getThumbnailUrl();
                if (url.length() > 0) {
                    Picasso.with(getContext()).load(url).fit().error(R.drawable.unknown_artist).into(thumbnailView);
                }
            }

            if (titleView != null) {
                titleView.setText(p.getName());
            }

        }

        return v;
    }
}
