package com.fiuba.tdp.petadopt.fragments.addPet.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiuba.tdp.petadopt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;

public class ChooseLocationMapFragment extends Fragment implements GoogleMap.OnCameraChangeListener, OnMapReadyCallback{


    private SupportMapFragment mapFragment;

    public ChooseLocationMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        //Use the child fragment manager since its a nested fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.regular_map);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.v("Location", cameraPosition.target.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
