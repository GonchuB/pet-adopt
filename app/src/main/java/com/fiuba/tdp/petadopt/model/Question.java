package com.fiuba.tdp.petadopt.model;

import android.util.Log;

import com.fiuba.tdp.petadopt.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Question {
    private String text;
    private Date created;
    private Answer answer;

    public String getText() {
        return text;
    }

    public static Question fromJson(JSONObject jsonObject) {
        Question question = new Question();
        try {
            question.text = jsonObject.getString("body");
            question.created = DateUtils.dateFromString(jsonObject.getString("created_at"));
            if (jsonObject.has("answer")) {
                question.answer = Answer.fromJson(jsonObject.getJSONObject("answer"));
            }
        } catch (JSONException e) {
            question.text = "Download failed";
            Log.e("Error parsing question", e.getLocalizedMessage());
        }
        return question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public Date getCreatedAt() {
        return created;
    }
}
