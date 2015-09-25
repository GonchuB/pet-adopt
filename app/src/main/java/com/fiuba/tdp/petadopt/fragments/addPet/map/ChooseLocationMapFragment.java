package com.fiuba.tdp.petadopt.fragments.addPet.map;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fiuba.tdp.petadopt.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ChooseLocationMapFragment extends Fragment implements GoogleMap.OnCameraChangeListener, OnMapReadyCallback {

    private TextView addressTextView;

    public ChooseLocationMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        //Use the child fragment manager since its a nested fragment
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.regular_map);
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
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                if (address.getThoroughfare() != null
                        && address.getSubThoroughfare() != null
                        && address.getLocality() != null
                        && address.getSubLocality() != null) {
                    addressTextView.setText(getAddressAsString(address));
                }
            }

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
}
