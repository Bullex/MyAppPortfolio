package com.alex.abumov.myappportfolio;

public class SpotifyArtistItem {
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

}
