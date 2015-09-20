package com.fiuba.tdp.petadopt.model;

/**
 * Created by lucas on 9/5/15.
 * Singleton class representing currently logged user
 */
public class User {
    private static User user;
    private String facebookId;
    private String facebookToken;
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
        }
        return user;
    }

    public void loggedInWithFacebook(String facebookId, String facebookToken){
        this.facebookId = facebookId;
        this.facebookToken = getFacebookToken();
    }
}
