package model;

/**
 * Created by lucas on 9/5/15.
 * Singleton class representing currently logged user
 */
public class User {
    private static User user;
    private String facebookId;
    private String facebookToken;

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
