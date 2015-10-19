package com.fiuba.tdp.petadopt.service;


import android.content.Context;
import android.util.Log;

import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by joaquinstankus on 13/09/15.
 */
public class UserClient extends HttpClient {

    private static UserClient userClient;

    public static UserClient instance() {
        if (userClient == null) {
            userClient = new UserClient();
        }
        return userClient;
    }


    public void signUp(Context context, String fb_id, String fb_token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/users.json");
        JSONObject user = new JSONObject();
        JSONObject userData = new JSONObject();
        StringEntity entity;
        try {
            userData.put("facebook_id", fb_id);
            userData.put("facebook_token", fb_token);
            user.put("user", userData);
            Log.v("JSON", user.toString());
            entity = new StringEntity(user.toString());
            client.post(context, url, entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updatePushToken(User user, String newToken, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("/users.json");
            url = url + "?user_token="+user.getAuthToken();
            JSONObject userObject = new JSONObject();
            JSONObject userTokenData = new JSONObject();
            userTokenData.put("device_id", newToken);
            userObject.put("user", userTokenData);
            StringEntity entity = new StringEntity(userObject.toString());
            entity.setContentEncoding("utf8");
            client.put(ActivityContext, url, entity, "application/json", handler);
        } catch (Exception e) {
            Log.e("Error in put request", e.getLocalizedMessage());
        }
    }


    public void login(Context context) {

    }

}
