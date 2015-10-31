package com.fiuba.tdp.petadopt.fragments;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.detail.PetDetailFragment;

public class MyPetsFragment extends PetResultFragment {
    public MyPetsFragment(){
        onItemClickHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PetDetailFragment petDetailFragment = new PetDetailFragment();
                petDetailFragment.setComingFromMyPetsScreen(true);
                petDetailFragment.setPet(pets.get(position));
                getActivity().setTitle(pets.get(position).getName());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.content_frame, petDetailFragment, "Choose location");
                ft.addToBackStack(null);
                ft.commit();
            }
        };
    }
}
