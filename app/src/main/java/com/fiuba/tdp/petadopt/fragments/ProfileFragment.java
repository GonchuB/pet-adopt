package com.fiuba.tdp.petadopt.fragments;

/**
 * Created by joaquinstankus on 07/09/15.
 */
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiuba.tdp.petadopt.R;
import com.rey.material.widget.EditText;

public class ProfileFragment extends Fragment {

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        populateFields(rootView);

        return rootView;
    }

    private void populateFields(View rootView) {


        EditText editText = (EditText) rootView.findViewById(R.id.user_name);
        String value = "JASON DERULO";
        editText.setText(value);
    }
}
