package com.fiuba.tdp.petadopt.fragments;

/**
 * Created by joaquinstankus on 07/09/15.
 */

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.activities.MainActivity;
import com.fiuba.tdp.petadopt.model.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.widget.Button;
import android.widget.Toast;

import com.rey.material.widget.EditText;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        populateFields(rootView);
        setSubmitListener(rootView);

        return rootView;
    }

    private void setSubmitListener(final View rootView) {
        Button submit = (Button) rootView.findViewById(R.id.pet_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) rootView.findViewById(R.id.user_name);
                User.user().setFirstName(editText.getText().toString());
                editText = (EditText) rootView.findViewById(R.id.user_last_name);
                User.user().setLastName(editText.getText().toString());
                editText = (EditText) rootView.findViewById(R.id.user_email);
                User.user().setEmail(editText.getText().toString());
                editText = (EditText) rootView.findViewById(R.id.user_phone);
                User.user().setPhone(editText.getText().toString());

                final ProgressDialog progress = new ProgressDialog(v.getContext());
                progress.setTitle(R.string.loading);
                progress.show();

                User.user().updateUserProfile(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, JSONObject body) {
                        Toast.makeText(getActivity(), R.string.profile_updated,
                                Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        MainActivity activity = (MainActivity) getActivity();
                        activity.goBackToHome();
                    }

                    @Override
                    public void onFailure(int code, Header[] headers, Throwable t, JSONObject body) {
                        progress.dismiss();
                        if (code == HttpStatus.SC_UNAUTHORIZED) {
                            Toast.makeText(getActivity(), R.string.auth_error, Toast.LENGTH_LONG).show();
                            ((MainActivity) getActivity()).goBackToLogin();
                        } else {
                            try {
                                if (body.has("email")) {

                                    JSONArray emailErrors = body.getJSONArray("email");
                                    String error = (String) emailErrors.get(0);

                                    if (error.equals("no es válido")) {
                                        Toast.makeText(getActivity(), R.string.invalid_email,
                                                Toast.LENGTH_LONG).show();
                                    }

                                    if (error.equals("ya está en uso")) {
                                        Toast.makeText(getActivity(), R.string.email_in_use,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
            }
        });
    }

    private void populateFields(final View rootView) {

        final ProgressDialog progress = new ProgressDialog(rootView.getContext());
        progress.setTitle(R.string.loading);
        progress.show();
        User.user().getUserProfile(new JsonHttpResponseHandler() {

            public void onSuccess(int code, Header[] headers, JSONObject body) {
                try {
                    EditText editText = (EditText) rootView.findViewById(R.id.user_name);
                    String first_name = body.getString("first_name");
                    if (!first_name.equals("null")) {
                        editText.setText(first_name);
                    }
                    editText = (EditText) rootView.findViewById(R.id.user_last_name);
                    String last_name = body.getString("last_name");
                    if (!last_name.equals("null")) {
                        editText.setText(last_name);
                    }
                    editText = (EditText) rootView.findViewById(R.id.user_email);
                    String email = body.getString("email");
                    if (!email.equals("")) {
                        editText.setText(email);
                    }
                    editText = (EditText) rootView.findViewById(R.id.user_phone);
                    String phone = body.getString("phone");
                    if (!phone.equals("null")) {
                        editText.setText(phone);
                    }
                    progress.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    Toast.makeText(getActivity(), R.string.auth_error, Toast.LENGTH_LONG).show();
                    ((MainActivity) getActivity()).goBackToLogin();
                } else {
                    Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_LONG).show();
                }
            }


        });


    }
}
