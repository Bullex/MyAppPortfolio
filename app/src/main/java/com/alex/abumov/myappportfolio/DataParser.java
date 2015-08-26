package com.alex.abumov.myappportfolio;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.alex.abumov.myappportfolio.Spotify.SpotifyArtistItem;
import com.alex.abumov.myappportfolio.Spotify.SpotifyTrackItem;
import com.alex.abumov.myappportfolio.Spotify.data.SpotifyContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

public class DataParser {

    public DataParser(){

    }

    public ArrayList<SpotifyArtistItem> getSearchDataFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String ND_ARTISTS= "artists";
        final String ND_ITEMS = "items";
        final String ND_IMAGES = "images";
        final String ND_NAME = "name";
        final String ND_ID = "id";
        final String ND_POPUL = "popularity";
        final String ND_IMAGES_URL = "url";


        JSONObject dataJson = new JSONObject(jsonStr);
        JSONObject artistsMainArray = dataJson.getJSONObject(ND_ARTISTS);
        JSONArray itemsArray = artistsMainArray.getJSONArray(ND_ITEMS);

        ArrayList<SpotifyArtistItem> resultArray = new ArrayList<>();
        for(int i = 0; i < itemsArray.length(); i++) {

            String imageUrl = "";
            String id;
            String name;
            Integer popularity;

            // curr item
            JSONObject item = itemsArray.getJSONObject(i);
            // images
            JSONArray imagesArray = item.getJSONArray(ND_IMAGES);
            // last image obj and get image url
            if(imagesArray.length() > 0) {
                JSONObject lastImageObj = imagesArray.getJSONObject(imagesArray.length() - 1);
                imageUrl = lastImageObj.getString(ND_IMAGES_URL);
            }
            // get artist id
            id = item.getString(ND_ID);
            // get artist name
            name = item.getString(ND_NAME);
            // get artist popularity
            popularity = item.getInt(ND_POPUL);

            // create new Artist Obj andd add it to result array
            resultArray.add(new SpotifyArtistItem(id, name, imageUrl, popularity));
        }

        return resultArray;
    }

    long addArtist(Context context, String ext_id, String name){
        long id = -1;

        String[] projection = new String[]{
                SpotifyContract.ArtistEntry.TABLE_NAME + "." + SpotifyContract.ArtistEntry._ID
        };
        String where = SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID + " = ?";
        String[] whereArgs = new String[]{
                ext_id
        };
        Cursor cursor = context.getContentResolver().query(
                SpotifyContract.ArtistEntry.CONTENT_URI,
                projection, where, whereArgs, null);
        if (cursor.moveToFirst()){
            id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        ContentValues values = new ContentValues();
        values.put(SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID, ext_id);
        values.put(SpotifyContract.ArtistEntry.COLUMN_DESCRIPTION, name);
        Uri insertedUri = context.getContentResolver().insert(SpotifyContract.ArtistEntry.CONTENT_URI, values);
        id = ContentUris.parseId(insertedUri);
        return id;
    }

    public ArrayList<SpotifyTrackItem> getTracksDataFromJson(Context context, String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String ND_TRACKS= "tracks";
        final String ND_ALBUM = "album";
        final String ND_ARTISTS = "artists";
        final String ND_ARTISTS_NAME = "name";
        final String ND_ARTISTS_ID = "id";
        final String ND_ALBUM_NAME = "name";
        final String ND_IMAGES = "images";
        final String ND_IMAGES_URL = "url";
        final String ND_ID = "id";
        final String ND_NAME = "name";
        final String ND_PREVIEW_URL = "preview_url";
        final String ND_POPUL = "popularity";

        JSONObject dataJson = new JSONObject(jsonStr);
        JSONArray tracksMainArray = dataJson.getJSONArray(ND_TRACKS);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(tracksMainArray.length());
        ArrayList<SpotifyTrackItem> resultArray = new ArrayList<>();
        for(int i = 0; i < tracksMainArray.length(); i++) {

            String imageUrl = "";
            String bigImageUrl = "";
            String artist = "";
            String artist_id = "";
            long db_artist_id = 0;
            String id;
            String name;
            Integer popularity;
            String album = "";
            String previewUrl;

            // curr item
            JSONObject item = tracksMainArray.getJSONObject(i);
            // album
            JSONObject albumObject = item.getJSONObject(ND_ALBUM);
            // last image obj and get image url
            if(albumObject != null) {
                album = albumObject.getString(ND_ALBUM_NAME);

                // images
                JSONArray imagesArray = albumObject.getJSONArray(ND_IMAGES);
                // last image obj and get image url
                if(imagesArray.length() > 0) {
                    JSONObject lastImageObj = imagesArray.getJSONObject(imagesArray.length() - 1);
                    imageUrl = lastImageObj.getString(ND_IMAGES_URL);
                    JSONObject firstImageObj = imagesArray.getJSONObject(0);
                    bigImageUrl = firstImageObj.getString(ND_IMAGES_URL);
                }
            }
            // album
            JSONArray artistArray = item.getJSONArray(ND_ARTISTS);
            // last image obj and get image url
            if(artistArray.length() > 0) {
                JSONObject artistObject = artistArray.getJSONObject(0);
                artist = artistObject.getString(ND_ARTISTS_NAME);
                artist_id = artistObject.getString(ND_ARTISTS_ID);
                db_artist_id = addArtist(context, artist_id, artist);
            }
            // get track id
            id = item.getString(ND_ID);
            // get track name
            name = item.getString(ND_NAME);
            // get track popularity
            popularity = item.getInt(ND_POPUL);
            // get track preview url
            previewUrl = item.getString(ND_PREVIEW_URL);

            // create new Artist Obj andd add it to result array
            resultArray.add(new SpotifyTrackItem(id, name, album, artist, artist_id, imageUrl, bigImageUrl, popularity, previewUrl));

            ContentValues artistValues = new ContentValues();

            artistValues.put(SpotifyContract.TrackEntry.COLUMN_EXTERNAL_ID, id);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_ARTIST_ID, db_artist_id);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_DESCRIPTION, name);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_ALBUM, album);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_IMAGE_URL, bigImageUrl);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_POPULARITY, popularity);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_PREVIEW_URL, previewUrl);
            artistValues.put(SpotifyContract.TrackEntry.COLUMN_THUMBNAIL_URL, imageUrl);

            cVVector.add(artistValues);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            // Student: call bulkInsert to add the weatherEntries to the database here
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            context.getContentResolver().bulkInsert(SpotifyContract.TrackEntry.CONTENT_URI, cvArray);
        }

        return resultArray;
    }
}
