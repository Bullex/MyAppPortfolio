package com.alex.abumov.myappportfolio.Spotify;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alex.abumov.myappportfolio.R;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyPlayerActivityFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener{

    static private PlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;


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
            musicSrv.playSong();

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


    private int mTrackIndex;
    private String mArtistId = "";
    private String mArtistName = "";
    private String mAlbumName = "";
    private String mTracktName = "";
    private String mTracktThumbnailUrl = "";
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
    final public static String ARTIST_NAME = "artist_name";
    final public static String ALBUM_NAME = "album_name";
    final public static String TRACK_NAME = "track_name";
    final public static String TRACK_THUMBNAIL_URL = "track_thumbnail_url";
    final public static String TRACK_PREVIEW_URL = "track_preview_url";

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


            }
        });
        nextBTN = (ImageButton) rootView.findViewById(R.id.ss_player_next);
        playBTN = (ImageButton) rootView.findViewById(R.id.ss_player_play);
        playBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv.isPlaying()){
                    musicSrv.pauseMusic();
                    playBTN.setImageResource(android.R.drawable.ic_media_play);
                }else{
                    if (musicSrv.isStopped()){
                        musicSrv.playSong();
                    }else {
                        musicSrv.startMusic();
                    }
                    playBTN.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        playerSB = (SeekBar) rootView.findViewById(R.id.ss_seek_bar);
        playerSB.setOnSeekBarChangeListener(this);

        if (intent != null && intent.hasExtra(ARTIST_ID)) {
            mTrackIndex = intent.getIntExtra(TRACK_INDEX, 0);
            mArtistId = intent.getStringExtra(ARTIST_ID);
            mArtistName = intent.getStringExtra(ARTIST_NAME);
            mAlbumName = intent.getStringExtra(ALBUM_NAME);
            mTracktName = intent.getStringExtra(TRACK_NAME);
            mTracktThumbnailUrl = intent.getStringExtra(TRACK_THUMBNAIL_URL);
            mTrackPreviewUrl = intent.getStringExtra(TRACK_PREVIEW_URL);
        } else {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mTrackIndex = getArguments().getInt(TRACK_INDEX, 0);
                mArtistId = getArguments().getString(ARTIST_ID, "");
                mArtistName = getArguments().getString(ARTIST_NAME, "");
                mAlbumName = getArguments().getString(ALBUM_NAME, "");
                mTracktName = getArguments().getString(TRACK_NAME, "");
                mTracktThumbnailUrl = getArguments().getString(TRACK_THUMBNAIL_URL, "");
                mTrackPreviewUrl = getArguments().getString(TRACK_PREVIEW_URL, "");
            }
        }

        artistTV.setText(mArtistName);
        albumTV.setText(mAlbumName);
        trackTV.setText(mTracktName);
        if (thumbnailIV != null) {
            if (mTracktThumbnailUrl.length() > 0) {
                Picasso.with(getActivity()).load(mTracktThumbnailUrl).fit().error(R.drawable.unknown_artist).into(thumbnailIV);
            }
        }
        return rootView;
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
        if(playIntent==null){
            playIntent = new Intent(getActivity(), PlayerService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(playIntent);
        musicSrv = null;
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
        if (fromUser) {
            seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

