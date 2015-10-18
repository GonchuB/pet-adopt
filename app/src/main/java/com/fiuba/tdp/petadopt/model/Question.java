package com.fiuba.tdp.petadopt.model;

import android.util.Log;

import com.fiuba.tdp.petadopt.util.DateUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Question {
    private String text;
    private Date created;
    private String askerName;
    private Answer answer;

    public Question(){

    }
    public Question(String text){
        this.text = text;
    }

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
            JSONObject userObject = jsonObject.getJSONObject("user");
            question.askerName = userObject.getString("full_name");
            if (question.askerName.equals("")){
                question.askerName = "Anónimo";
            }
        } catch (JSONException e) {
            question.text = "Download failed";
            Log.e("Error parsing question", e.getLocalizedMessage());
        }
        return question;
    }

    public String toJson(){

        JsonObject result = new JsonObject();
        JsonObject petQuestion = new JsonObject();

        petQuestion.addProperty("body", text);
        result.add("pet_question", petQuestion);

        return result.toString();
    }

    public Answer getAnswer() {
        return answer;
    }

    public Date getCreatedAt() {
        return created;
    }

    public String getAsker() {
        return askerName;
    }
}
