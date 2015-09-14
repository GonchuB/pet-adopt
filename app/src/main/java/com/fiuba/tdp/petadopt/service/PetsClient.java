/**
 * Created by joaquinstankus on 06/09/15.
 * Example HTTP Client
 */

package com.fiuba.tdp.petadopt.service;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class PetsClient extends HttpClient {

    private String auth_token;

    public PetsClient(String auth_token) {
        this.auth_token = auth_token;
    }

    public void getPets(JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        RequestParams params = new RequestParams();
        Log.v("after intent", auth_token);
        params.put("user_token", auth_token);
        client.get(url, params, handler);
    }
}
