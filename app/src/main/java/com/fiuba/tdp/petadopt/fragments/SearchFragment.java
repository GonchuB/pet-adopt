package com.fiuba.tdp.petadopt.fragments;

/**
 * Created by joaquinstankus on 07/09/15.
 */
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private ListView lv;

    public SearchFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        final Button searchButton = (Button) rootView.findViewById(R.id.submit);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitSearch(v);
            }
        });

        lv = (ListView) rootView.findViewById(R.id.pet_list);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.advance_search_action:
                // do s.th.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void submitSearch(View view){
        EditText queryTextView = (EditText) getActivity().findViewById(R.id.query);
        String query = queryTextView.getText().toString();
        PetsClient client = PetsClient.instance();

        client.simpleQueryPets(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                ArrayList<Pet> pets = parsePets(body);
                ArrayAdapter<Pet> adapter = new ArrayAdapter<Pet>(getActivity(), android.R.layout.simple_list_item_1, pets);
                lv.setAdapter(adapter);
            }
        });



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
