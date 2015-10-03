package com.fiuba.tdp.petadopt.fragments;

/**
 * Created by joaquinstankus on 07/09/15.
 */
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.service.PetListItemAdapter;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultFragment extends Fragment {

    private ListView lv;
    private List<Pet> pets = null;

    public ResultFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
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
    public void onStart() {
        super.onStart();
        renderResults();
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
        if (query.length() == 0) {
            Toast toast = Toast.makeText(getContext(), R.string.search_error, Toast.LENGTH_SHORT);
            toast.show();
        }
        PetsClient client = PetsClient.instance();
        final ProgressDialog progress = new ProgressDialog(view.getContext());
        progress.setTitle(R.string.loading);
        progress.show();

        client.simpleQueryPets(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                progress.dismiss();
                setResults(body);
                renderResults();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                Toast toast = Toast.makeText(getContext(), R.string.search_error, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

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
