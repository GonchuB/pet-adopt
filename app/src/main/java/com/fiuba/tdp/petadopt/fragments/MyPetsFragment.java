package com.fiuba.tdp.petadopt.fragments;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

public class MyPetsFragment extends PetResultFragment {
    public MyPetsFragment(){ onItemClickHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedId = pets.get(position).getId();
            final AdopterResultFragment adopterResultFragment = new AdopterResultFragment();
            adopterResultFragment.setPet(pets.get(position));
            PetsClient client = PetsClient.instance();
            client.getAdoptersForPet(selectedId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int code, Header[] headers, JSONArray body) {
                    adopterResultFragment.setResults(body);
                    adopterResultFragment.onStart();
                }
            });
            getActivity().setTitle(pets.get(position).getName() + " - " + getActivity().getString(R.string.requesters_title));
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.add(R.id.content_frame, adopterResultFragment, "Adopter Result Fragment");
            ft.addToBackStack(null);
            ft.commit();
        }
    };}
}
