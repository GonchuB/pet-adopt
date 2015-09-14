package com.fiuba.tdp.petadopt.service;

import com.fiuba.tdp.petadopt.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by joaquinstankus on 13/09/15.
 */

public class HttpClient {

    protected AsyncHttpClient client;

    public HttpClient(){
        client = new AsyncHttpClient();
    }
    public String getApiUrl(String relativeUrl) {
        return BuildConfig.BASE_ENDPOINT + relativeUrl;
    }
}
