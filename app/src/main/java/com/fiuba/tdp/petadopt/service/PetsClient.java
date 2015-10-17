/**
 * Created by joaquinstankus on 06/09/15.
 * Example HTTP Client
 */

package com.fiuba.tdp.petadopt.service;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.entity.StringEntity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PetsClient extends HttpClient {


    private String auth_token;
    private static PetsClient singletonClient;

    private PetsClient() {

    }

    public static PetsClient instance() {
        if (singletonClient == null) {
            singletonClient = new PetsClient();
        }
        return singletonClient;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
        //client.addHeader("user_token", auth_token);
    }

    public void getPetsForHome(JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets/top.json");
        RequestParams params = new RequestParams();
        client.get(url, params, handler);
    }

    public void getPet(String petId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets/" + petId + ".json");
        RequestParams params = new RequestParams();
        client.get(url, params, handler);
    }

    public void getQuestions(String petId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets/" + petId + "/questions.json");
        RequestParams params = new RequestParams();
        client.get(url, params, handler);
    }

    public void getMyPets(JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        User user = User.user();
        RequestParams params = new RequestParams();
        params.add("user_id", String.valueOf(user.getId()));
        client.get(url, params, handler);
    }

    public void getMyRequestedPets(JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        User user = User.user();
        RequestParams params = new RequestParams();
        params.add("user_id", String.valueOf(user.getId()));
        params.add("with_adoption_requests", String.valueOf(true));
        client.get(url, params, handler);
    }

    @Override
    public String getApiUrl(String relativeUrl) {
        String url = super.getApiUrl(relativeUrl);
        if (auth_token != null) {
            return url + "?user_token=" + auth_token;
        }
        return url;
    }

    public void createPet(Pet pet, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("/pets.json");
            StringEntity entity = new StringEntity(pet.toJson());
            entity.setContentEncoding("utf8");
            client.post(ActivityContext, url, entity, "application/json", handler);
        } catch (UnsupportedEncodingException e) {
            Log.e("Error in post request", e.getLocalizedMessage());
        }
    }

    public void simpleQueryPets(String query, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        RequestParams params = new RequestParams();
        params.put("metadata", query);
        Log.v("after intent", auth_token);
        client.get(url, params, handler);
    }

    public void advanceSearch(HashMap<String, String> petFilter, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        RequestParams params = buildParameters(petFilter);
        Log.v("after intent", auth_token);
        client.get(url, params, handler);
    }

    private RequestParams buildParameters(HashMap<String, String> petFilter) {
        RequestParams params = new RequestParams();
        Iterator it = petFilter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            params.add(pair.getKey().toString(), pair.getValue().toString());
        }

        return params;
    }

    public void uploadImage(String petId, String path, JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets/" + petId + "/images.json");

        RequestParams params = new RequestParams();

        try {
            params.put("pet_image[image]", new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(url, params, handler);

    }
}
