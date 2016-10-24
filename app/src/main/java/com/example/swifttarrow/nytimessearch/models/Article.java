package com.example.swifttarrow.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swifttarrow on 10/20/2016.
 */

public class Article implements Serializable {
    String webUrl;
    String headline;
    String thumbNail;

    public Article(JSONObject jsonObject){
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0){
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbNail = null;
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static List<Article> fromJSONArray(JSONArray jsonArray){
        List<Article> result = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++){
            try {
                result.add(new Article(jsonArray.getJSONObject(i)));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }
}
