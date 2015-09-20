package com.fiuba.tdp.petadopt.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.fiuba.tdp.petadopt.model.User;

/**
 * Created by tomas on 18/09/15.
 */
public class UserPersistenceService {


    private final static String USER_DATA = "user_data";
    private final static String USER_PRESENT = "user_present";
    private final static String USER_FB_ID = "user_fb_id";
    private final static String USER_FB_TOKEN = "user_fb_token";
    private final static String USER_AUTH_TOKEN = "user_auth_token";

    Context currentContext;

    public UserPersistenceService(Context context) {
        currentContext = context;
    }

    public User getUserIfPresent() {
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        boolean userPresent = userData.getBoolean(USER_PRESENT, false);
        if (userPresent) {
            String userFacebookId = userData.getString(USER_FB_ID,"");
            String userFacebookToken = userData.getString(USER_FB_TOKEN,"");
            String userAuthToken = userData.getString(USER_AUTH_TOKEN,"");
            User user = User.user();
            user.loggedInWithFacebook(userFacebookId, userFacebookToken);
            user.setAuthToken(userAuthToken);
            return user;
        }

        return null;
    }

    public void saveUserData(User user) {
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putBoolean(USER_PRESENT, true);
        editor.putString(USER_FB_ID, user.getFacebookId());
        editor.putString(USER_FB_TOKEN, user.getFacebookToken());
        editor.putString(USER_AUTH_TOKEN, user.getAuthToken());
        editor.apply();
    }

    public void destroyUserData() {
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putBoolean(USER_PRESENT, false);
        editor.putString(USER_FB_ID, "");
        editor.putString(USER_FB_TOKEN, "");
        editor.putString(USER_AUTH_TOKEN, "");
        editor.apply();
    }

}
