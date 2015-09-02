package com.alex.abumov.myappportfolio.Spotify.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by Alexander on 24.08.2015.
 */
public class SpotifyContract {

    public static final String CONTENT_AUTHORITY = "com.alex.abumov.myappportfolio.Spotify";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ARTIST = "artist";
    public static final String PATH_TRACK = "track";

    public static long normalizeDate(long startDate) {
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static final class TrackEntry implements BaseColumns {
        public static final String TABLE_NAME = "track";

        public static final String COLUMN_EXTERNAL_ID = "tr_external_id";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_DESCRIPTION = "tr_description";
        public static final String COLUMN_ALBUM = "tr_album";
        public static final String COLUMN_THUMBNAIL_URL = "tr_thumbnail_url";
        public static final String COLUMN_IMAGE_URL = "tr_album_image_url";
        public static final String COLUMN_POPULARITY = "tr_popularity";
        public static final String COLUMN_PREVIEW_URL = "tr_preview_url";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static Uri buildTrackUri(String artist_external_id, String tr_external_id) {
            return CONTENT_URI.buildUpon().appendPath(artist_external_id).appendPath(tr_external_id).build();
        }
    }

    public static final class ArtistEntry implements BaseColumns {

        public static final String TABLE_NAME = "artist";

        public static final String COLUMN_EXTERNAL_ID = "art_external_id";
        public static final String COLUMN_DESCRIPTION = "art_description";
        public static final String COLUMN_THUMBNAIL_URL = "art_thumbnail_url";
        public static final String COLUMN_POPULARITY = "art_popularity";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;


        public static Uri buildArtistUri(String artist_external_id) {
            return CONTENT_URI.buildUpon().appendPath(artist_external_id).build();
        }

        public static Uri buildArtistUriById(long _id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(_id)).build();
        }

        public static Uri buildArtistTrackUri(String artist_external_id, String track_external_id) {
            return CONTENT_URI.buildUpon().appendPath(artist_external_id)
                    .appendQueryParameter(COLUMN_EXTERNAL_ID, track_external_id).build();
        }

        public static String getArtistExternalIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getTrackExternalIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
}

