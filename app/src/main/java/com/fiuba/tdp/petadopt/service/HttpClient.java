package com.fiuba.tdp.petadopt.service;

import android.content.Context;

import com.fiuba.tdp.petadopt.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;


public class HttpClient {
    public static Context ActivityContext;
    protected AsyncHttpClient client;
    public static String base_url = null;

    public HttpClient() {
        client = new AsyncHttpClient();
    }

    public String getApiUrl(String relativeUrl) {
        if (base_url == null) {
            base_url = BuildConfig.BASE_ENDPOINT;
            return BuildConfig.BASE_ENDPOINT + relativeUrl;
        } else {
            return base_url + relativeUrl;
        }

    }

    public class UTF8StringEntity extends StringEntity {
        public UTF8StringEntity(String string) throws UnsupportedEncodingException {
            super(string, "UTF-8");
        }
    }
}
