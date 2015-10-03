package com.fiuba.tdp.petadopt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.service.PetListItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private ListView lv;
    private List<Pet> pets = null;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

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
        String[] from = {"line_1", "line_2"};
        int[] to = {R.id.name, R.id.image};
        if (pets != null && pets.size() != 0) {
            ArrayAdapter adapter = new PetListItemAdapter(getActivity(), pets);
            lv.setAdapter(adapter);
            getActivity().findViewById(R.id.no_results).setVisibility(View.INVISIBLE);
        } else {
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), new ArrayList<HashMap<String, String>>(), R.layout.pet_list_item, from, to);
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
