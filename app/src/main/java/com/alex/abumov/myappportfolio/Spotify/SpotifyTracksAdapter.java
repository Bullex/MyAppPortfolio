package com.alex.abumov.myappportfolio.Spotify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.abumov.myappportfolio.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class SpotifyTracksAdapter extends ArrayAdapter<SpotifyTrackItem> {

    public SpotifyTracksAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SpotifyTracksAdapter(Context context, int resource, List<SpotifyTrackItem> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.spotify_track_list_view_row, null);
        }

        SpotifyTrackItem p = getItem(position);

        if (p != null) {
            ImageView thumbnailView  = (ImageView) v.findViewById(R.id.ss_thumbnail);
            TextView titleView = (TextView) v.findViewById(R.id.ss_title);
            TextView albumView = (TextView) v.findViewById(R.id.ss_album);

            if (thumbnailView != null) {
                String url = p.getThumbnailUrl();
                if (url.length() > 0) {
                    Picasso.with(getContext()).load(url).fit().error(R.drawable.unknown_artist).into(thumbnailView);
                }
            }

            if (titleView != null) {
                titleView.setText(p.getName());
            }

            if (albumView != null) {
                albumView.setText(p.getAlbum());
            }

        }

        return v;
    }
}
