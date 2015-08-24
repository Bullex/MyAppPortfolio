package com.alex.abumov.myappportfolio.Spotify;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

public class PlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener{

    public static final String EXTRA_MESSENGER="com.alex.abumov.player.EXTRA_MESSENGER";
    MediaPlayer mMediaPlayer = null;
    private final IBinder musicBind = new MusicBinder();
    private int mBufferPosition;

    // indicates the state our service:
    enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped, // media player is stopped and not prepared to play
        Preparing, // media player is preparing...
        Prepared, // media player is prepared
        Playing, // playback active (media player ready!). (but the media player may actually be
        // paused in this state if we don't have audio focus. But we stay in this state
        // so that we know we have to resume playback once we get focus back)
        Paused
        // playback paused (media player ready!)
    };

    State mState = State.Retrieving;

    private ArrayList<SpotifyTrackItem> mTracks;
    private int trackPosn;

    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        trackPosn = 0;
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }



    public void setList(ArrayList<SpotifyTrackItem> tracks){
        mTracks = tracks;
    }

    public void setSong(int trackIndex){
        trackPosn = trackIndex;
    }

    public class MusicBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void playSong(){
        mMediaPlayer.reset();
        //get song
        SpotifyTrackItem playSong = mTracks.get(trackPosn);
        String trackUrl = playSong.getPreviewUrl();
        try{
            mMediaPlayer.setDataSource(trackUrl);
        }
        catch(Exception e){
            Log.e("SPOTIFY MUSIC SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
        mState = State.Preparing;
    }

    protected void setBufferPosition(int progress) {
        mBufferPosition = progress;
    }

    /** Called when MediaPlayer is ready */
    @Override
    public void onPrepared(MediaPlayer player) {
        mState = State.Prepared;
    }

    public boolean isStatePrepared(){
        return mState.equals(State.Prepared);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mState = State.Retrieving;
    }

    public void pauseMusic() {
        if (mState.equals(State.Playing)) {
            mMediaPlayer.pause();
            mState = State.Paused;
        }
    }

    public void startMusic() {
        if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
            mMediaPlayer.start();
            mState = State.Playing;
        }
    }

    public boolean isPlaying() {
        if (mState.equals(State.Playing)) {
            return true;
        }
        return false;
    }

    public int getMusicDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getBufferPercentage() {
        return mBufferPosition;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPosition(percent * getMusicDuration() / 100);
    }

}
