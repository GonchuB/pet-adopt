package com.fiuba.tdp.petadopt.fragments.search;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.activities.MainActivity;
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.LocationChosenDelegate;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class AdvancedSearchFragment extends Fragment {
    private HashMap<String, String> petFilter = new HashMap<String, String>();
    private View rootView;
    public AdvancedSearchFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        setHasOptionsMenu(true);

        populateSpinner(rootView, R.id.pet_type, R.array.search_pet_type_array);
        populateSpinner(rootView, R.id.pet_gender, R.array.search_pet_gender_array);
        populateSpinner(rootView, R.id.pet_main_color, R.array.search_pet_color_array);
        TextView locationView = (TextView) rootView.findViewById(R.id.chosen_location);
        final Button button = (Button) rootView.findViewById(R.id.choose_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseLocationMapFragment mapFragment = new ChooseLocationMapFragment();
                mapFragment.setLocationChosenDelegate(new LocationChosenDelegate() {
                    @Override
                    public void locationWasChosen(LatLng location, String address) {
                        petFilter.put("location",String.valueOf(location.latitude) + "," + String.valueOf(location.longitude));
                        TextView locationView = (TextView) rootView.findViewById(R.id.chosen_location);
                        locationView.setText(address);
                    }
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, mapFragment, "Choose location");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        setUpPetFillingCallbacks(rootView);
        setupSubmitButton(rootView);

        return rootView;
    }

    private void setupSubmitButton(View rootView) {
        final Button button = (Button) rootView.findViewById(R.id.search_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (petFilter.size() <= 0) {
                    Toast toast = Toast.makeText(getContext(), R.string.advance_search_no_filter, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                PetsClient client = PetsClient.instance();
                final ProgressDialog progress = new ProgressDialog(v.getContext());
                progress.setTitle(R.string.loading);
                progress.show();
                client.advanceSearch(petFilter, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, JSONArray body) {

                        progress.dismiss();
                        MainActivity ma = (MainActivity) getActivity();
                        ma.showResults(body);
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
        });
    }

    private void setUpPetFillingCallbacks(View rootView) {
        Spinner spinner = (Spinner) rootView.findViewById(R.id.pet_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.search_pet_type_array);
                if (position != 0) {
                    if (position == 1) petFilter.put("type", "Cat");
                    if (position == 2) petFilter.put("type", "Dog");
                } else {
                    petFilter.remove("type");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        spinner.setSelection(0);

        spinner = (Spinner) rootView.findViewById(R.id.pet_gender);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.search_pet_gender_array);
                if (position > 0) {
                    if (position == 1) petFilter.put("gender", "male");
                    if (position == 2) petFilter.put("gender", "female");
                } else {
                    petFilter.remove("gender");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(0);

        spinner = (Spinner) rootView.findViewById(R.id.pet_main_color);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.search_pet_color_array);
                if (position > 0) {
                    petFilter.put("colors", petTypes[position]);
                } else {
                    petFilter.remove("colors");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        spinner.setSelection(0);

        final EditText ageEditText = (EditText) rootView.findViewById(R.id.pet_age);
        ageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                petFilter.put("age", ageEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void populateSpinner(View rootView, int viewId, int arrayId) {
        Spinner spinner = (Spinner) rootView.findViewById(viewId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                arrayId, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.advanced_search_fragment_actions, menu);
    }
}
