package com.fiuba.tdp.petadopt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Adopter;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.service.AdopterListItemAdapter;
import com.fiuba.tdp.petadopt.service.PetListItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdopterResultFragment extends Fragment {

    protected ListView lv;
    protected List<Adopter> adopters = null;

    public AdopterResultFragment(){}

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
        adopters = parseAdopters(body);
    }

    private void renderResults() {
        if (adopters != null && adopters.size() != 0) {
            ArrayAdapter adapter = new AdopterListItemAdapter(getActivity(), adopters);
            lv.setAdapter(adapter);
            getActivity().findViewById(R.id.no_results).setVisibility(View.INVISIBLE);
        } else {
            ArrayAdapter adapter = new AdopterListItemAdapter(getActivity(), new ArrayList<Adopter>());
            lv.setAdapter(adapter);
            getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Adopter> parseAdopters(JSONArray adoptersArray) {
        ArrayList<Adopter> adopters = new ArrayList<>(adoptersArray.length());
        for(int i = 0; i < adoptersArray.length(); i++) {
            try {
                JSONObject adoptantJSON = adoptersArray.getJSONObject(i);
                Adopter adopter = new Adopter();
                adopter.loadFromJSON(adoptantJSON);
                adopters.add(i, adopter);
            } catch (JSONException e) {
                // FIXME
                Log.e("Error parsing pet",e.getLocalizedMessage());
            }
        }

        return adopters;
    }
}
