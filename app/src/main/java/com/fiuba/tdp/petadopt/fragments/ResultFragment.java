package com.fiuba.tdp.petadopt.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.service.PetListItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultFragment extends Fragment {

    private ListView lv;
    private List<Pet> pets = null;

    public ResultFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        lv = (ListView) rootView.findViewById(R.id.pet_list);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        renderResults();
    }

    public void setResults(JSONArray body) {
        pets = parsePets(body);
    }

    private void renderResults() {
        if (pets != null && pets.size() != 0) {
            ArrayAdapter adapter = new PetListItemAdapter(getActivity(), pets);
            lv.setAdapter(adapter);
            getActivity().findViewById(R.id.no_results).setVisibility(View.INVISIBLE);
        } else {
            ArrayAdapter adapter = new PetListItemAdapter(getActivity(), new ArrayList<Pet>());
            lv.setAdapter(adapter);
            getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Pet> parsePets(JSONArray petArray) {
        ArrayList<Pet> pets = new ArrayList<>(petArray.length());
        for(int i = 0; i < petArray.length(); i++) {
            try {
                JSONObject petJSON = petArray.getJSONObject(i);
                Pet pet = new Pet();
                pet.loadFromJSON(petJSON);
                pets.add(i,pet);
            } catch (JSONException e) {
                // FIXME
                e.printStackTrace();
            }
        }

        return pets;
    }
}
