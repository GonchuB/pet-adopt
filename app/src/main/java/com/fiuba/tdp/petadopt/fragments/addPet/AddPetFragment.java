package com.fiuba.tdp.petadopt.fragments.addPet;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.LocationChosenDelegate;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

@SuppressWarnings("ALL")
public class AddPetFragment extends Fragment {


    public AddPetFragment() {
    }

    private Pet pet = new Pet();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_pet, container, false);

        setUpPetFillingCallbacks(rootView);

        populateSpinner(rootView, R.id.pet_type, R.array.pet_type_array);
        populateSpinner(rootView, R.id.pet_gender, R.array.pet_gender_array);
        populateSpinner(rootView, R.id.pet_main_color, R.array.pet_color_array);
        populateSpinner(rootView, R.id.pet_second_color, R.array.pet_color_array);
        TextView locationView = (TextView)rootView.findViewById(R.id.chosen_location);
        final Button button = (Button) rootView.findViewById(R.id.choose_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseLocationMapFragment mapFragment = new ChooseLocationMapFragment();
                mapFragment.setLocationChosenDelegate(new LocationChosenDelegate() {
                    @Override
                    public void locationWasChosen(LatLng location, String address) {
                        pet.setLocation(location);
                        TextView locationView = (TextView)rootView.findViewById(R.id.chosen_location);
                        locationView.setText(address);
                    }
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, mapFragment, "Choose location");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        setupSubmitButton(rootView);
        return rootView;
    }

    private void setupSubmitButton(View rootView) {
        final Button button = (Button) rootView.findViewById(R.id.pet_submit);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(v.getContext());
                progress.setTitle(R.string.loading);
                progress.show();
                PetsClient.instance().createPet(pet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, JSONArray body) {
                        progress.dismiss();
                        String items = "";
                        items = body.toString();
                        Log.v("Pet created", pet.toJson());

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        progress.dismiss();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getContext(), R.string.pet_creation_error, duration);
                        toast.show();
                        Log.e("Error creating pet", pet.toJson());
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
                String[] petTypes = getResources().getStringArray(R.array.pet_type_array);
                pet.setType(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner = (Spinner) rootView.findViewById(R.id.pet_gender);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_gender_array);
                pet.setGender(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner = (Spinner) rootView.findViewById(R.id.pet_main_color);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_color_array);
                pet.setFirstColor(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner = (Spinner) rootView.findViewById(R.id.pet_second_color);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] petTypes = getResources().getStringArray(R.array.pet_color_array);
                pet.setSecondColor(petTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final EditText editText = (EditText) rootView.findViewById(R.id.pet_name);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setName(editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final EditText ageEditText = (EditText) rootView.findViewById(R.id.pet_name);
        ageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setAge(ageEditText.getText().toString());
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

}
