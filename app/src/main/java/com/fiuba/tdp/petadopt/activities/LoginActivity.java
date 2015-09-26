package com.fiuba.tdp.petadopt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fiuba.tdp.petadopt.R;

import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.AuthClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    public final static String AUTH_TOKEN = "com.fiuba.tdp.petadopt.AUTH_TOKEN";
    public static final String EXIT_FLAG = "exit_flag";
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AuthClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.user().isLoggedIn()) {
            continueToHome();
            return;
        }

        //FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String facebookId = loginResult.getAccessToken().getUserId();
                String facebookToken = loginResult.getAccessToken().getToken();
                Log.v("FB",
                        "User ID: "
                                + facebookId
                                + " " +
                                "Auth Token: "
                                + facebookToken
                );
                User.user().loggedInWithFacebook(facebookId, facebookToken);
                client = new AuthClient();
                client.signUp(getApplicationContext(), facebookId, facebookToken, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int code, Header[] headers, JSONObject body) {
                                String auth_token = "";
                                try {
                                    auth_token = body.getString("authentication_token");
                                    User.user().setAuthToken(auth_token);
                                    Log.v("JSON", body.toString());
                                    Log.v("authtok", auth_token);
                                    continueToHome();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                Log.v("Error signing in", responseString);
                            }
                        }
                );
            }

            @Override
            public void onCancel() {
                Log.v("FB", "Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.v("FB", "Login attempt failed.");
            }
        });


    }

    private void continueToHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

}
