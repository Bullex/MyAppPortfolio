package com.alex.abumov.myappportfolio.Spotify;

import android.content.Context;
import android.text.TextUtils;
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

    ViewHolder holder;

    static class ViewHolder {
        ImageView thumbnailView;
        TextView titleView;
        TextView albumView;
    }

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
            holder = new ViewHolder();
        }else{
            holder = (ViewHolder) v.getTag();
        }

        SpotifyTrackItem p = getItem(position);

        if (p != null) {
            holder.thumbnailView  = (ImageView) v.findViewById(R.id.ss_thumbnail);
            holder.titleView = (TextView) v.findViewById(R.id.ss_title);
            holder.albumView = (TextView) v.findViewById(R.id.ss_album);

            if (holder.thumbnailView != null) {
                String url = p.getThumbnailUrl();
                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(getContext()).load(url).fit().error(R.drawable.unknown_artist).into(holder.thumbnailView);
                }
            }

            if (holder.titleView != null) {
                holder.titleView.setText(p.getName());
            }

            if (holder.albumView != null) {
                holder.albumView.setText(p.getAlbum());
            }
            v.setTag(holder);
        }

        return v;
    }
}
