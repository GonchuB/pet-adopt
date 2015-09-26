package com.fiuba.tdp.petadopt.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lucas on 9/5/15.
 * Singleton class representing currently logged user
 */
public class User {
    private static User user;
    private String facebookId;
    private String facebookToken;
    private String authToken;
    public static Context currentContext;

    private final static String USER_DATA = "user_data";
    private final static String USER_PRESENT = "user_present";
    private final static String USER_FB_ID = "user_fb_id";
    private final static String USER_FB_TOKEN = "user_fb_token";
    private final static String USER_AUTH_TOKEN = "user_auth_token";


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        save();
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    private User(){
    }

    public static User user(){
        if (user == null) {
            user = new User();
            SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
            boolean userPresent = userData.getBoolean(USER_PRESENT, false);
            if (userPresent) {
                user.facebookId = userData.getString(USER_FB_ID,"");
                user.facebookToken = userData.getString(USER_FB_TOKEN,"");
                user.authToken = userData.getString(USER_AUTH_TOKEN,"");
            }
            return user;
        }
        return user;
    }

    public void save(){
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putBoolean(USER_PRESENT, true);
        editor.putString(USER_FB_ID, facebookId);
        editor.putString(USER_FB_TOKEN, facebookToken);
        editor.putString(USER_AUTH_TOKEN, authToken);
        editor.apply();
    }

    public void logout() {
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putBoolean(USER_PRESENT, false);
        editor.putString(USER_FB_ID, "");
        editor.putString(USER_FB_TOKEN, "");
        editor.putString(USER_AUTH_TOKEN, "");
        editor.apply();
    }

    public Boolean isLoggedIn(){
        return authToken != null;
    }

    public void loggedInWithFacebook(String facebookId, String facebookToken){
        this.facebookId = facebookId;
        this.facebookToken = getFacebookToken();
        save();
    }
}
