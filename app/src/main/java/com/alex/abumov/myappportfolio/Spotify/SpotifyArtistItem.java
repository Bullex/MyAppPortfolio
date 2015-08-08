package com.alex.abumov.myappportfolio.Spotify;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyArtistItem implements Parcelable {
    private String thumbnailUrl;
    private String name;
    private Integer popularity;
    private String id;

    public SpotifyArtistItem(String id, String name, String thumbnailUrl, Integer popularity){
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.popularity = popularity;
    }

    private SpotifyArtistItem(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.thumbnailUrl = in.readString();
        this.popularity = in.readInt();
    }

    public String getThumbnailUrl(){
        return this.thumbnailUrl;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public Integer getPopularity(){
        return this.popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(thumbnailUrl);
        out.writeInt(popularity);
    }

    public static final Parcelable.Creator<SpotifyArtistItem> CREATOR = new Parcelable.Creator<SpotifyArtistItem>() {
        public SpotifyArtistItem createFromParcel(Parcel in) {
            return new SpotifyArtistItem(in);
        }

        public SpotifyArtistItem[] newArray(int size) {
            return new SpotifyArtistItem[size];
        }
    };
}
