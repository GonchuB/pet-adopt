package com.fiuba.tdp.petadopt.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fiuba.tdp.petadopt.R;

import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.HttpClient;
import com.fiuba.tdp.petadopt.service.UserClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    public final static String AUTH_TOKEN = "com.fiuba.tdp.petadopt.AUTH_TOKEN";
    public static final String EXIT_FLAG = "exit_flag";
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private UserClient client;
    private int clicks; // DEBUGGING PURPOSES

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (User.user().isLoggedIn()) {
            continueToHome();
            return;
        }



//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null)
//            actionBar.hide();

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
                client = UserClient.instance();
                client.signUp(getApplicationContext(), facebookId, facebookToken, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int code, Header[] headers, final JSONObject body) {
                                try {
                                    client.testAuthToken(body.getString("authentication_token"), new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int code, Header[] headers, JSONArray content) {
                                            User user = User.user();
                                            user.loadInfoFromJSON(body);
                                            user.save();
                                            Log.v("JSON", body.toString());
                                            Log.v("authtok", user.getAuthToken());
                                            continueToHome();
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                            LoginManager.getInstance().logOut();
                                            Toast.makeText(LoginActivity.this, R.string.blocked_login, Toast.LENGTH_LONG).show();
                                        }
                                    });

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

        // DEBUG PURPOSES
        final EditText endpointInput = (EditText) findViewById(R.id.endpoint_input);
        endpointInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d("Pet Adopt", "Changing endpoint to " + v.getText().toString());
                    String url = v.getText().toString();
                    if (url.isEmpty()) {
                        HttpClient.base_url = null;
                    } else {
                        HttpClient.base_url = url;
                    }
                    endpointInput.setVisibility(View.GONE);
                    SharedPreferences endpointData = getSharedPreferences("endpoint", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = endpointData.edit();
                    editor.putString("url", HttpClient.base_url);
                    editor.apply();
                }
                return false;
            }
        });


    }

    private void continueToHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void byPassLogin(View view) {
        String harcodedAuthToken = "CAJgH_NsxWNR_edmuzvZ";
        User.user().loggedInWithFacebook("HarcodedFB_ID", "HarcodedFB_Token");
        User.user().setAuthToken(harcodedAuthToken);
        continueToHome();
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


    // DEBUG PURPOSES
    public void debugOnClick(View view) {
        clicks += 1;
        if (clicks >= 3) {
            EditText endpointInput = (EditText) findViewById(R.id.endpoint_input);
            endpointInput.setVisibility(View.VISIBLE);
        }

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    clicks = 0;
                }
            },
            500);
    }
}
