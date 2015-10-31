package com.fiuba.tdp.petadopt.service;

import com.fiuba.tdp.petadopt.model.Adopter;
import com.fiuba.tdp.petadopt.model.Pet;

/**
 * Created by tomas on 10/30/15.
 */
public interface AdopterConfirmDelegate {
    public void confirmAdoption(Adopter adopter);
}
