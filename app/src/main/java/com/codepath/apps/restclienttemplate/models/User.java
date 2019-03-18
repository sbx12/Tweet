package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    public  User(){

    }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        String profileImage = jsonObject.getString("profile_image_url");
        user.profileImageUrl = profileImage.replaceAll("_normal", "_bigger");
        Log.d("image" , user.profileImageUrl);
        return  user;
    }
}
