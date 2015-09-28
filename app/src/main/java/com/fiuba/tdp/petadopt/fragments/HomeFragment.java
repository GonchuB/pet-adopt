package com.fiuba.tdp.petadopt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (pets != null && pets.size() != 0) {
            List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < pets.size(); i++) {
                HashMap<String, String> m = new HashMap<String, String>();
                m.put("line_1", pets.get(i).toString());
                m.put("line_2", pets.get(i).getColors());
                data.add(m);
            }
            String[] from = {"line_1", "line_2"};
            int[] to = {R.id.line_1, R.id.line_2};
            List<? extends Map<String, ?>> castedData = (List<? extends Map<String, ?>>) data;
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), castedData, R.layout.pet_list_item, from, to);
            lv.setAdapter(adapter);
            getActivity().findViewById(R.id.no_results).setVisibility(View.INVISIBLE);
        } else {
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
