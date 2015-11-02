package com.fiuba.tdp.petadopt.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.fiuba.tdp.petadopt.BuildConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucas on 9/5/15.
 * Singleton class representing currently logged user
 */
public class User {
    private static User user;
    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String facebookId;
    private String facebookToken;
    private String authToken;
    public static Context currentContext;

    private static final String USER_ID = "user_id";
    private static final String USER_FIRST_NAME = "user_first_name";
    private static final String USER_LAST_NAME = "user_last_name";
    private static final String USER_PHONE = "user_phone";
    private static final String USER_EMAIL = "user_email";
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

    private User() {
    }

    public static User user() {
        if (user == null) {
            user = new User();
            SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
            boolean userPresent = userData.getBoolean(USER_PRESENT, false);
            if (userPresent) {
                user.id = userData.getInt(USER_ID, 0);
                user.firstName = userData.getString(USER_FIRST_NAME, "");
                user.lastName = userData.getString(USER_LAST_NAME, "");
                user.phone = userData.getString(USER_PHONE, "");
                user.email = userData.getString(USER_EMAIL, "");
                user.facebookId = userData.getString(USER_FB_ID, "");
                user.facebookToken = userData.getString(USER_FB_TOKEN, "");
                user.authToken = userData.getString(USER_AUTH_TOKEN, "");
            }
            return user;
        }
        return user;
    }

    public void save() {
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putBoolean(USER_PRESENT, true);
        if (id != null) {
            editor.putInt(USER_ID, id);
        }
        editor.putString(USER_FIRST_NAME, firstName);
        editor.putString(USER_LAST_NAME, lastName);
        editor.putString(USER_PHONE, phone);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_FB_ID, facebookId);
        editor.putString(USER_FB_TOKEN, facebookToken);
        editor.putString(USER_AUTH_TOKEN, authToken);
        editor.apply();
    }

    public void logout() {
        SharedPreferences userData = currentContext.getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userData.edit();
        editor.putInt(USER_ID, 0);
        editor.putString(USER_FIRST_NAME, "");
        editor.putString(USER_LAST_NAME, "");
        editor.putString(USER_PHONE, "");
        editor.putString(USER_EMAIL, "");
        editor.putBoolean(USER_PRESENT, false);
        editor.putString(USER_FB_ID, "");
        editor.putString(USER_FB_TOKEN, "");
        editor.putString(USER_AUTH_TOKEN, "");
        authToken = null;
        editor.apply();
    }

    public Boolean isLoggedIn() {
        return authToken != null;
    }

    public void loggedInWithFacebook(String facebookId, String facebookToken) {
        this.facebookId = facebookId;
        this.facebookToken = getFacebookToken();
        save();
    }

    public void getUserProfile(JsonHttpResponseHandler handler) {
        String url = BuildConfig.BASE_ENDPOINT + "/users/profile.json" + "?user_token=" + authToken;
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            client.get(url, handler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserProfile(Context context, JSONObject user, final JsonHttpResponseHandler jsonHttpResponseHandler) {
        String url = BuildConfig.BASE_ENDPOINT + "/users/profile.json" + "?user_token=" + authToken;
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            StringEntity se = new StringEntity(user.toString());
            client.put(context, url, se, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int code, Header[] headers, JSONObject body) {
                    try {
                        JSONObject u = body.getJSONObject("user");
                        firstName = u.getString("first_name");
                        lastName = u.getString("last_name");
                        email = u.getString("email");
                        phone = u.getString("phone");
                        save();
                        jsonHttpResponseHandler.onSuccess(code, headers, body);
                    } catch (JSONException e) {
                        jsonHttpResponseHandler.onFailure(code, headers, e, body);
                    }
                }

                @Override
                public void onFailure(int code, Header[] headers, Throwable t, JSONObject body) {
                    jsonHttpResponseHandler.onFailure(code, headers, t, body);
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void loadInfoFromJSON(JSONObject info) {
        try {
            this.id = info.getInt("id");
            this.firstName = info.getString("first_name");
            this.lastName = info.getString("first_name");
            this.phone = info.getString("phone");
            this.email = info.getString("email");
            this.authToken = info.getString("authentication_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public boolean missingInfo() {
        Boolean missing = false;
        if (firstName == null || firstName.equals("null") || firstName.equals("")) {
            missing = true;
        }

        if (lastName == null || lastName.equals("null") || lastName.equals("")) {
            missing = true;
        }

        if (phone == null || phone.equals("null") || phone.equals("")) {
            missing = true;
        }

        if (email == null || email.equals("null") || email.equals("")) {
            missing = true;
        }

        return missing;
    }

    public boolean ownsPet(Pet pet) {
        return pet.getUserId().equals(String.valueOf(id));
    }

}
