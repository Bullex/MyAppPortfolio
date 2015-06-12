package com.alex.abumov.myappportfolio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataParser {

    public DataParser(){

    }

    public ArrayList<SpotifyArtistItem> getSearchDataFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String ND_ARTISTS= "artists";
        final String ND_ITEMS = "items";
        final String ND_IMAGES = "images";
        final String ND_NAME = "name";
        final String ND_ID = "id";
        final String ND_POPUL = "popularity";
        final String ND_IMAGES_URL = "url";

        JSONObject dataJson = new JSONObject(jsonStr);
        JSONObject artistsMainArray = dataJson.getJSONObject(ND_ARTISTS);
        JSONArray itemsArray = artistsMainArray.getJSONArray(ND_ITEMS);

        ArrayList<SpotifyArtistItem> resultArray = new ArrayList<>();
        for(int i = 0; i < itemsArray.length(); i++) {

            String imageUrl = "";
            String id;
            String name;
            Integer popularity;

            // curr item
            JSONObject item = itemsArray.getJSONObject(i);
            // images
            JSONArray imagesArray = item.getJSONArray(ND_IMAGES);
            // last image obj and get image url
            if(imagesArray.length() > 0) {
                JSONObject lastImageObj = imagesArray.getJSONObject(imagesArray.length() - 1);
                imageUrl = lastImageObj.getString(ND_IMAGES_URL);
            }
            // get artist id
            id = item.getString(ND_ID);
            // get artist name
            name = item.getString(ND_NAME);
            // get artist popularity
            popularity = item.getInt(ND_POPUL);

            // create new Artist Obj andd add it to result array
            resultArray.add(new SpotifyArtistItem(id, name, imageUrl, popularity));
        }
        return resultArray;
    }
}
