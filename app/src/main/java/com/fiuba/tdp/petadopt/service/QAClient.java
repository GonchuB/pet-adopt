package com.fiuba.tdp.petadopt.service;


import com.fiuba.tdp.petadopt.model.Question;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

public class QAClient extends HttpClient {
    private String auth_token;
    private static QAClient singletonClient;

    private QAClient() {

    }

    public static QAClient instance() {
        if (singletonClient == null) {
            singletonClient = new QAClient();
        }
        return singletonClient;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    @Override
    public String getApiUrl(String relativeUrl) {
        String url = super.getApiUrl(relativeUrl);
        if (auth_token != null) {
            return url + "?user_token=" + auth_token;
        }
        return url;
    }

    public void postQuestion(String id, Question question, JsonHttpResponseHandler handler){
        String url = getApiUrl("/pets/"+id+"/questions.json");

        try {
            StringEntity entity = new StringEntity(question.toJson());
            entity.setContentEncoding("utf8");
            client.post(ActivityContext, url, entity, "application/json", handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

}
