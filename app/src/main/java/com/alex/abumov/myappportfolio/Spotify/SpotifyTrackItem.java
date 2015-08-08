package com.alex.abumov.myappportfolio.Spotify;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrackItem implements Parcelable {

    private String id;
    private String name;
    private String album;
    private String thumbnailUrl;
    private Integer popularity;
    private String previewUrl;

    public SpotifyTrackItem(String id, String name, String album, String thumbnailUrl, Integer popularity, String previewUrl){
        this.id = id;
        this.name = name;
        this.album = album;
        this.thumbnailUrl = thumbnailUrl;
        this.popularity = popularity;
        this.previewUrl = previewUrl;
    }

    private SpotifyTrackItem(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.album = in.readString();
        this.thumbnailUrl = in.readString();
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

    public String getThumbnailUrl(){
        return this.thumbnailUrl;
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
        out.writeString(thumbnailUrl);
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
