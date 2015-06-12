package com.alex.abumov.myappportfolio;

public class SpotifyArtistItem {
    private String thumbnailUrl;
    private String name;

    public SpotifyArtistItem(String thumbnailUrl, String name){
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
    }

    public String getThumbnailUrl(){
        return this.thumbnailUrl;
    }

    public String getName(){
        return this.name;
    }

}
