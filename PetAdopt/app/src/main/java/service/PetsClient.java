/**
 * Created by joaquinstankus on 06/09/15.
 * Example HTTP Client
 */

package service;

import com.fiuba.tdp.petadopt.BuildConfig;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class PetsClient {

    private final String API_BASE_URL = BuildConfig.BASE_ENDPOINT;
    private AsyncHttpClient client;

    public PetsClient() {
        this.client = new AsyncHttpClient();
    }

    public void getPublications(JsonHttpResponseHandler handler) {
        String url = getApiUrl("/pets.json");
        RequestParams params = new RequestParams();
        client.get(url, params, handler);
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }


}
