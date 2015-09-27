/**
 * Created by joaquinstankus on 06/09/15.
 * Example HTTP Client
 */

package com.fiuba.tdp.petadopt.service;

import android.util.Log;

import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;


public class PetsClient extends HttpClient {



    private String auth_token;
    private static PetsClient singletonClient;

    private PetsClient(){

    }

    public static PetsClient instance() {
        if (singletonClient==null){
            singletonClient = new PetsClient();
        }
        return singletonClient;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
        client.addHeader("user_token",auth_token);
    }

    public void getPets(JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        RequestParams params = new RequestParams();
        Log.v("after intent", auth_token);
        params.put("user_token", User.user().getAuthToken());
        client.get(url, params, handler);
    }


    public void createPet(Pet pet, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("/pets.json");
            StringEntity entity = new StringEntity(pet.toJson());
            client.post(ActivityContext, url, entity, "application/json", handler);
        } catch (UnsupportedEncodingException e) {
            Log.e("Error in post request", e.getLocalizedMessage());
        }
    }
}
