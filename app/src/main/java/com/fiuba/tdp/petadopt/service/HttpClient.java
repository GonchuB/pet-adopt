package com.fiuba.tdp.petadopt.service;

import android.content.Context;

import com.fiuba.tdp.petadopt.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;


public class HttpClient {
    public static Context ActivityContext;
    protected AsyncHttpClient client;

    public HttpClient(){
        client = new AsyncHttpClient();
    }
    public String getApiUrl(String relativeUrl) {
        return BuildConfig.BASE_ENDPOINT + relativeUrl;
    }


}
