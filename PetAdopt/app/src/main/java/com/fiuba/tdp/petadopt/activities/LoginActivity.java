package com.fiuba.tdp.petadopt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fiuba.tdp.petadopt.R;

import com.fiuba.tdp.petadopt.model.User;


public class LoginActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.fiuba.tdp.petadopt.MESSAGE";
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "communicating activities");
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.v("FB","Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.v("FB","Login attempt failed.");
            }
        });


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

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void showMainScreen(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
