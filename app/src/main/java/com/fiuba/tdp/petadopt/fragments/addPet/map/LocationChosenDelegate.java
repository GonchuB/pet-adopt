package com.fiuba.tdp.petadopt.fragments.addPet.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lucas on 9/26/15.
 */
public interface LocationChosenDelegate {
    public void locationWasChosen(LatLng location, String address);
    public void locationWasAccepted();
}
