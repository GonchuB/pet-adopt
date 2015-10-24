package com.fiuba.tdp.petadopt.model;

import android.util.Log;

import com.fiuba.tdp.petadopt.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Answer {
    private String text;
    private Date created;

    public static Answer fromJson(JSONObject jsonObject) {
        Answer answer = new Answer();
        try {
            answer.text = jsonObject.getString("body");
            answer.created = DateUtils.dateFromString(jsonObject.getString("created_at"));
        } catch (JSONException e) {
            answer.text = "Error downloading answer";
            Log.e("Error parsing answer", e.getLocalizedMessage());
        }
        return answer;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedAt() {
        return created;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
