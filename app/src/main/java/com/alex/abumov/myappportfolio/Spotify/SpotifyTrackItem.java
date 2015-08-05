package com.alex.abumov.myappportfolio.Spotify;

public class SpotifyTrackItem {

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
}
