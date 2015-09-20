package com.fiuba.tdp.petadopt.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiuba.tdp.petadopt.R;

/**
 * Created by tomas on 19/09/15.
 */
public class AddPetFragment extends Fragment {

    public AddPetFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_pet, container, false);

        return rootView;
    }

}
