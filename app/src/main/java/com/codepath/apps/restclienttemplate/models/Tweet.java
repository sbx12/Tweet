package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Tweet {

    public String body;
    public long uid;
    public String createdAt;
    public User user;
    public String reTweetCount;
    public String favoriteCount;
    public String mediaUrl;
    public String mediaType;
    public boolean hasMedia;

    public Tweet(){

    }

    public static  Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.reTweetCount = jsonObject.getString("retweet_count");
        tweet.favoriteCount = jsonObject.getString("favorite_count");
        tweet.hasMedia = false;
        if(jsonObject.getJSONObject("entities").has("media")) {
            tweet.mediaUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
            Log.d("url", tweet.mediaUrl + "   " + tweet.mediaType);
            tweet.mediaType = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("type");
            Log.d("url", tweet.mediaUrl + "   " + tweet.mediaType);
            tweet.hasMedia = true;
        }


        return tweet;
    }

    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(createdAt);
    }

}
