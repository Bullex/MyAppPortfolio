package com.alex.abumov.myappportfolio.Spotify.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexander on 24.08.2015.
 */
public class SpotifyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "spotify2.db";

    public SpotifyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ARTIST_TABLE = "CREATE TABLE " + SpotifyContract.ArtistEntry.TABLE_NAME + " (" +
                SpotifyContract.ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID + " TEXT UNIQUE NOT NULL, " +
                SpotifyContract.ArtistEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_ARTIST_TABLE);

        final String SQL_CREATE_TRACK_TABLE = "CREATE TABLE " + SpotifyContract.TrackEntry.TABLE_NAME + " (" +
                SpotifyContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SpotifyContract.TrackEntry.COLUMN_EXTERNAL_ID + " TEXT UNIQUE NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_ALBUM + " TEXT NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_POPULARITY + " TEXT NOT NULL, " +
                SpotifyContract.TrackEntry.COLUMN_PREVIEW_URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + SpotifyContract.TrackEntry.COLUMN_ARTIST_ID + ") REFERENCES " +
                SpotifyContract.ArtistEntry.TABLE_NAME + " (" + SpotifyContract.ArtistEntry._ID + ") " +
                " UNIQUE (" + SpotifyContract.TrackEntry.COLUMN_EXTERNAL_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.ArtistEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SpotifyContract.TrackEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
