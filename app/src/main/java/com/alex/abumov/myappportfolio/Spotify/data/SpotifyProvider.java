package com.alex.abumov.myappportfolio.Spotify.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


/**
 * Created by Alexander on 24.08.2015.
 */
public class SpotifyProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SpotifyDbHelper mOpenHelper;

    static final int ARTIST_TRACK_LIST = 100;
    static final int ARTIST_TRACK_INFO = 101;
    static final int ARTIST = 102;
    static final int TRACK = 103;

    private static final SQLiteQueryBuilder sArtistWithTracksQueryBuilder;
    private static final SQLiteQueryBuilder sArtistQueryBuilder;

    static{
        sArtistWithTracksQueryBuilder = new SQLiteQueryBuilder();

        sArtistWithTracksQueryBuilder.setTables(
                SpotifyContract.ArtistEntry.TABLE_NAME + " INNER JOIN " +
                        SpotifyContract.TrackEntry.TABLE_NAME +
                        " ON " + SpotifyContract.ArtistEntry.TABLE_NAME +
                        "." + SpotifyContract.ArtistEntry._ID +
                        " = " + SpotifyContract.TrackEntry.TABLE_NAME +
                        "." + SpotifyContract.TrackEntry.COLUMN_ARTIST_ID);

        sArtistQueryBuilder = new SQLiteQueryBuilder();

        sArtistQueryBuilder.setTables(
                SpotifyContract.ArtistEntry.TABLE_NAME);
    }

    //artist.external_id = ?
    private static final String sArtistTrackListSelection =
            SpotifyContract.ArtistEntry.TABLE_NAME+
                    "." + SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID + " = ? ";

    //artist.external_id = ? AND track.external_id = ?
    private static final String sArtistTrackInfoSelection =
            SpotifyContract.ArtistEntry.TABLE_NAME+
                    "." + SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID + " = ? AND " +
                    SpotifyContract.TrackEntry.COLUMN_EXTERNAL_ID + " = ? ";

    private Cursor getArtistTrackListByExternalId(Uri uri, String[] projection, String sortOrder) {
        String art_external_id = SpotifyContract.ArtistEntry.getArtistExternalIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sArtistTrackListSelection;
        selectionArgs = new String[]{art_external_id};

        return sArtistWithTracksQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArtistByExternalId(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return sArtistQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getArtistTrackInfoByExternalId(Uri uri, String[] projection, String sortOrder) {
        String art_external_id = SpotifyContract.ArtistEntry.getArtistExternalIdFromUri(uri);
        String tr_external_id = SpotifyContract.ArtistEntry.getTrackExternalIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sArtistTrackInfoSelection;
        selectionArgs = new String[]{art_external_id, tr_external_id};

        return sArtistWithTracksQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_ARTIST, ARTIST);
        uriMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_TRACK, TRACK);
        uriMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_ARTIST + "/*", ARTIST_TRACK_LIST);
        uriMatcher.addURI(SpotifyContract.CONTENT_AUTHORITY, SpotifyContract.PATH_TRACK + "/*/*", ARTIST_TRACK_INFO);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SpotifyDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTIST_TRACK_INFO:
                return SpotifyContract.TrackEntry.CONTENT_ITEM_TYPE;
            case ARTIST_TRACK_LIST:
                return SpotifyContract.ArtistEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ARTIST_TRACK_INFO:
            {
                retCursor = getArtistTrackInfoByExternalId(uri, projection, sortOrder);
                break;
            }
            case ARTIST_TRACK_LIST: {
                retCursor = getArtistTrackListByExternalId(uri, projection, sortOrder);
                break;
            }
            case ARTIST: {
                retCursor = getArtistByExternalId(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        String artist_external_id = "";
        if (values.containsKey(SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID)){
            artist_external_id = values.get(SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID).toString();
        }
        switch (match) {
            case ARTIST_TRACK_INFO:
            {
                long _id = db.insert(SpotifyContract.TrackEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SpotifyContract.TrackEntry.buildTrackUri(artist_external_id, values.get(SpotifyContract.TrackEntry.COLUMN_EXTERNAL_ID).toString());
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ARTIST_TRACK_LIST: {
                long _id = db.insert(SpotifyContract.ArtistEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SpotifyContract.ArtistEntry.buildArtistUri(artist_external_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ARTIST: {
                try {
                    long _id = db.insertOrThrow(SpotifyContract.ArtistEntry.TABLE_NAME, null, values);
                    if ( _id > 0 )
                        returnUri = SpotifyContract.ArtistEntry.buildArtistUriById(_id);
                    else
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                catch(SQLException e)
                {
                    // Sep 12, 2013 6:50:17 AM
                    Log.e("Exception", "SQLException" + String.valueOf(e.getMessage()));
                    e.printStackTrace();
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        if (selection == null){
            selection = "1";
        }

        switch (match) {
            case ARTIST_TRACK_INFO:
            {
                rowsDeleted = db.delete(SpotifyContract.TrackEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ARTIST_TRACK_LIST: {
                rowsDeleted = db.delete(SpotifyContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match) {
            case ARTIST_TRACK_INFO:
            {
                rowsUpdated = db.update(SpotifyContract.TrackEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ARTIST_TRACK_LIST: {
                rowsUpdated = db.update(SpotifyContract.ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ARTIST_TRACK_INFO:
            {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyContract.TrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case ARTIST_TRACK_LIST: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyContract.TrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case ARTIST: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyContract.ArtistEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRACK: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SpotifyContract.TrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
