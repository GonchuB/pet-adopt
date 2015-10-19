package com.fiuba.tdp.petadopt.fragments.detail;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.LocationChosenDelegate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rey.material.widget.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ShowLocationMapFragment extends ChooseLocationMapFragment {

    private LatLng petLocation;


    public ShowLocationMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shouldCenterInBA = false;
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ImageView centerPin = (ImageView) rootView.findViewById(R.id.map_pin);
        centerPin.setVisibility(View.INVISIBLE);
        TextView addressView = (TextView) rootView.findViewById(R.id.addressTextView);
        addressView.setVisibility(View.INVISIBLE);
        FloatingActionButton selectLocationButton = (FloatingActionButton) rootView.findViewById(R.id.select_location);
        selectLocationButton.setVisibility(View.INVISIBLE);
        progress.show();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                progress.dismiss();
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(petLocation.latitude, petLocation.longitude))
                        .title("Ubicación de la mascota"));
                LatLngBounds region = new LatLngBounds(
                        new LatLng(petLocation.latitude - 0.001, petLocation.longitude - 0.001), new LatLng(petLocation.latitude + 0.001, petLocation.longitude + 0.001));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(region, 0));
            }
        });
        return rootView;
    }

    public void setPetLocation(LatLng petLocation) {
        this.petLocation = petLocation;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }
}
