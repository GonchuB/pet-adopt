package com.fiuba.tdp.petadopt.fragments.addPet.map;

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
import android.widget.Button;
import android.widget.TextView;

import com.fiuba.tdp.petadopt.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rey.material.widget.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChooseLocationMapFragment extends Fragment implements GoogleMap.OnCameraChangeListener, OnMapReadyCallback {

    private TextView addressTextView;
    private LocationChosenDelegate locationChosenDelegate;
    ProgressDialog progress;
    public ChooseLocationMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        //Use the child fragment manager since its a nested fragment
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.regular_map);

        progress = new ProgressDialog(view.getContext());
        progress.setTitle(R.string.loading);
        progress.show();
        mapFragment.getMapAsync(this);
        mapFragment.getMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds BAIRES = new LatLngBounds(
                        new LatLng(-34.6033, -58.3817), new LatLng(-34.60, -58.38));
                mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(BAIRES, 0));
            }
        });
        addressTextView = (TextView) view.findViewById(R.id.addressTextView);
        FloatingActionButton selectLocationButton = (FloatingActionButton) view.findViewById(R.id.select_location);
        selectLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationChosenDelegate.locationWasAccepted();
            }
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(cameraPosition.target.latitude,
                    cameraPosition.target.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getThoroughfare() != null
                        && address.getSubThoroughfare() != null
                        && address.getLocality() != null
                        && address.getSubLocality() != null) {
                    addressTextView.setText(getAddressAsString(address));
                    if (locationChosenDelegate!=null){
                        locationChosenDelegate.locationWasChosen(cameraPosition.target, getAddressAsString(address));
                    }
                }
            }
            progress.dismiss();
        } catch (IOException e) {
            Log.e("Error geocoding coords", e.getLocalizedMessage());
        }
    }

    @NonNull
    private String getAddressAsString(Address address) {
        return address.getThoroughfare() + " " +
                address.getSubThoroughfare() + ", " +
                address.getSubLocality() + ", " +
                address.getLocality();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setLocationChosenDelegate(LocationChosenDelegate locationChosenDelegate) {
        this.locationChosenDelegate = locationChosenDelegate;
    }
}
