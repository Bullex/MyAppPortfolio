package com.alex.abumov.myappportfolio.Spotify;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrackItem implements Parcelable {

    private String id;
    private String name;
    private String album;
    private String artist;
    private String artist_id;
    private String thumbnailUrl;
    private String albumImgUrl;
    private Integer popularity;
    private String previewUrl;

    public SpotifyTrackItem(String id, String name, String album, String artist, String artist_id, String thumbnailUrl, String albumImgUrl, Integer popularity, String previewUrl){
        this.id = id;
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.artist_id = artist_id;
        this.thumbnailUrl = thumbnailUrl;
        this.albumImgUrl = albumImgUrl;
        this.popularity = popularity;
        this.previewUrl = previewUrl;
    }

    private SpotifyTrackItem(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.album = in.readString();
        this.artist = in.readString();
        this.artist_id = in.readString();
        this.thumbnailUrl = in.readString();
        this.albumImgUrl = in.readString();
        this.popularity = in.readInt();
        this.previewUrl = in.readString();
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getAlbum(){
        return this.album;
    }

    public String getArtist(){
        return this.artist;
    }

    public String getArtistId(){
        return this.artist_id;
    }

    public String getThumbnailUrl(){
        return this.thumbnailUrl;
    }

    public String getAlbumImgUrl(){
        return this.albumImgUrl;
    }

    public Integer getPopularity(){
        return this.popularity;
    }

    public String getPreviewUrl(){
        return this.previewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(album);
        out.writeString(artist);
        out.writeString(artist_id);
        out.writeString(thumbnailUrl);
        out.writeString(albumImgUrl);
        out.writeInt(popularity);
        out.writeString(previewUrl);
    }

    public static final Parcelable.Creator<SpotifyTrackItem> CREATOR = new Parcelable.Creator<SpotifyTrackItem>() {
        public SpotifyTrackItem createFromParcel(Parcel in) {
            return new SpotifyTrackItem(in);
        }

        public SpotifyTrackItem[] newArray(int size) {
            return new SpotifyTrackItem[size];
        }
    };
}
