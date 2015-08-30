package com.alex.abumov.myappportfolio.Spotify;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alex.abumov.myappportfolio.R;
import com.alex.abumov.myappportfolio.Spotify.data.SpotifyContract;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyPlayerActivityFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, SeekBar.OnSeekBarChangeListener{

    static private PlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    private SpotifyPlayerActivityFragment mPlayerFragment = this;


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(SpotifyTrackListActivityFragment.items);

            musicSrv.setSong(mTrackIndex);
            if (mOnRotate) {
                musicSrv.playSong((int) timeElapsed);
            }else{
                musicSrv.playSong(0);
            }

            durationHandler.postDelayed(updateSeekBarTime, 100);

            musicBound = true;
        }



        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private double timeElapsed = 0, finalTime = 0;
    private Handler durationHandler = new Handler();

    private static final int DETAIL_LOADER = 0;
    private static final String[] PLAYER_COLUMNS = {
            SpotifyContract.TrackEntry.TABLE_NAME + "." + SpotifyContract.TrackEntry._ID,
            SpotifyContract.TrackEntry.COLUMN_EXTERNAL_ID,
            SpotifyContract.ArtistEntry.COLUMN_EXTERNAL_ID,
            SpotifyContract.ArtistEntry.COLUMN_DESCRIPTION,
            SpotifyContract.TrackEntry.COLUMN_ALBUM,
            SpotifyContract.TrackEntry.COLUMN_DESCRIPTION,
            SpotifyContract.TrackEntry.COLUMN_IMAGE_URL,
            SpotifyContract.TrackEntry.COLUMN_POPULARITY,
            SpotifyContract.TrackEntry.COLUMN_PREVIEW_URL,
            SpotifyContract.TrackEntry.COLUMN_THUMBNAIL_URL
    };


    private int mTrackIndex = 0;
    private String mArtistId = "";
    private String mArtistName = "";
    private String mAlbumName = "";
    private String mTrackId = "";
    private String mTrackName = "";
    private String mTrackImageUrl = "";
    private String mTrackPreviewUrl = "";
    private TextView artistTV;
    private TextView albumTV;
    private TextView trackTV;
    private TextView durationTV;
    private ImageView thumbnailIV;
    private ImageButton playBTN;
    private ImageButton prevBTN;
    private ImageButton nextBTN;
    private SeekBar playerSB;


    final public static String TRACK_INDEX = "track_index";
    final public static String ARTIST_ID = "artist_id";
    final public static String TRACK_ID = "track_id";

    final private Integer DB_TRACK_INDEX = 0;
    final private Integer DB_TRACK_ID = 1;
    final private Integer DB_ARTIST_ID = 2;
    final private Integer DB_ARTIST_NAME = 3;
    final private Integer DB_ALBUM_NAME = 4;
    final private Integer DB_TRACK_NAME = 5;
    final private Integer DB_TRACK_IMAGE_URL = 6;
    final private Integer DB_TRACK_POPULARITY = 7;
    final private Integer DB_TRACK_PREVIEW_URL = 8;
    final private Integer DB_TRACK_THUMBNAIL_URL = 9;

    private boolean mOnRotate = false;

    public SpotifyPlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.spotify_player_screen, container, false);
        artistTV = (TextView) rootView.findViewById(R.id.ss_artist_name);
        albumTV = (TextView) rootView.findViewById(R.id.ss_album_name);
        trackTV = (TextView) rootView.findViewById(R.id.ss_track_name);
        durationTV = (TextView) rootView.findViewById(R.id.ss_player_duration);
        thumbnailIV = (ImageView) rootView.findViewById(R.id.ss_thumbnail);
        prevBTN = (ImageButton) rootView.findViewById(R.id.ss_player_prev);
        prevBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyTrackItem item = SpotifyTrackListActivityFragment.items.get(mTrackIndex-1);
                mTrackIndex--;
                mArtistId = item.getArtistId();
                mTrackId = item.getId();
                getLoaderManager().restartLoader(DETAIL_LOADER, null, mPlayerFragment);
                checkButtons();
                stopService();
                startService();
            }
        });
        nextBTN = (ImageButton) rootView.findViewById(R.id.ss_player_next);
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyTrackItem item = SpotifyTrackListActivityFragment.items.get(mTrackIndex+1);
                mTrackIndex++;
                mArtistId = item.getArtistId();
                mTrackId = item.getId();
                getLoaderManager().restartLoader(DETAIL_LOADER, null, mPlayerFragment);
                checkButtons();
                stopService();
                startService();
            }
        });
        playBTN = (ImageButton) rootView.findViewById(R.id.ss_player_play);
        playBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv.isPlaying()) {
                    musicSrv.pauseMusic();
                    playBTN.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (musicSrv.isStopped()) {
                        musicSrv.playSong(0);
                    } else {
                        musicSrv.startMusic();
                    }
                    playBTN.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        playerSB = (SeekBar) rootView.findViewById(R.id.ss_seek_bar);
        playerSB.setOnSeekBarChangeListener(this);

        if (savedInstanceState != null){
            mTrackIndex = savedInstanceState.getInt(TRACK_INDEX, 0);
            mArtistId = savedInstanceState.getString(ARTIST_ID);
            mTrackId = savedInstanceState.getString(TRACK_ID);
            timeElapsed = savedInstanceState.getInt("timeElapsed");
            mOnRotate = false;
        }else {
            if (intent != null && intent.hasExtra(ARTIST_ID)) {
                mTrackIndex = intent.getIntExtra(TRACK_INDEX, 0);
                mArtistId = intent.getStringExtra(ARTIST_ID);
                mTrackId = intent.getStringExtra(TRACK_ID);
            } else {
                Bundle arguments = getArguments();
                if (arguments != null) {
                    mTrackIndex = getArguments().getInt(TRACK_INDEX, 0);
                    mArtistId = getArguments().getString(ARTIST_ID);
                    mTrackId = getArguments().getString(TRACK_ID);
                }
            }
        }
        startService();
        checkButtons();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TRACK_INDEX, mTrackIndex);
        outState.putString(ARTIST_ID, mArtistId);
        outState.putString(TRACK_ID, mTrackId);
        outState.putInt("timeElapsed", (int)timeElapsed);
        mOnRotate = true;
        super.onSaveInstanceState(outState);
    }

    private void checkButtons(){
        if (mTrackIndex == 0){
            prevBTN.setClickable(false);
        }else{
            prevBTN.setClickable(true);
            if (mTrackIndex == SpotifyTrackListActivityFragment.items.size() - 1){
                nextBTN.setClickable(false);
            }else{
                nextBTN.setClickable(true);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, mPlayerFragment);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SpotifyMainActivity.mTwoPane){
            // safety check
            if (getDialog() == null) {
                return;
            }

            int dialogWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
            int dialogHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
    }

    private void startService() {
        if(playIntent==null) {
            playIntent = new Intent(getActivity(), PlayerService.class);
        }
        getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(playIntent);
    }

    private void stopService() {
        getActivity().stopService(playIntent);
        getActivity().unbindService(musicConnection);
        musicSrv = null;
    }


    @Override
    public void onDestroy() {
//        if (!mOnRotate) {
            stopService();
//        }else{
//            musicSrv.pauseMusic();
//        }
        super.onDestroy();
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if (musicSrv != null){
                if (musicSrv.isStatePrepared()) {
                    finalTime = musicSrv.getMusicDuration();
                    playerSB.setMax((int) finalTime);
                    playBTN.setClickable(true);
                    playBTN.setImageResource(android.R.drawable.ic_media_pause);
                    musicSrv.startMusic();
                }
                if (musicSrv.isPlaying()) {
                    //get current position
                    timeElapsed = musicSrv.getCurrentPosition();
                    //set seekbar progress
                    seekTo((int) timeElapsed);
                    //set time remaing
                    double timeRemaining = finalTime - timeElapsed;

                    durationTV.setText(String.format("%d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed)));

                    if (timeRemaining < 1000) {
                        seekTo(0);
                        musicSrv.stopMusic();
                        playBTN.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
                //repeat yourself that again in 100 miliseconds
                durationHandler.postDelayed(this, 100);
            }
        }
    };

    public void seekTo(int progress){
        playerSB.setProgress(progress);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (musicSrv != null && fromUser) {
            musicSrv.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null){
            return null;
        }
        Uri trackInfoUri = SpotifyContract.TrackEntry.buildTrackUri(
                mArtistId,
                mTrackId
        );
        return new CursorLoader(
                getActivity(),
                trackInfoUri,
                PLAYER_COLUMNS,
                null,
                null,
                null
        );
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mArtistName = data.getString(DB_ARTIST_NAME);
            mAlbumName = data.getString(DB_ALBUM_NAME);
            mTrackName = data.getString(DB_TRACK_NAME);
            mTrackImageUrl = data.getString(DB_TRACK_IMAGE_URL);

            artistTV.setText(mArtistName);
            albumTV.setText(mAlbumName);
            trackTV.setText(mTrackName);
            if (thumbnailIV != null) {
                if (mTrackImageUrl.length() > 0) {
                    Picasso.with(getActivity()).load(mTrackImageUrl).fit().error(R.drawable.unknown_artist).into(thumbnailIV);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}

