package com.fiuba.tdp.petadopt.service;


import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;


public class AuthClient extends HttpClient {

    public void signUp(Context context, String fb_id, String fb_token, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/users.json");
        JSONObject user = new JSONObject();
        JSONObject userData = new JSONObject();
        StringEntity entity;
        try {
            userData.put("facebook_id", fb_id);
            userData.put("facebook_token", fb_token);
            user.put("user", userData);
            Log.v("JSON",user.toString());
            entity = new UTF8StringEntity(user.toString());
            client.post(context, url, entity, "application/json", handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(Context context) {

    }

}
