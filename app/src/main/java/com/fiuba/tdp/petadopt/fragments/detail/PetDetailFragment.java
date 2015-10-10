package com.fiuba.tdp.petadopt.fragments.detail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.widget.OnChildSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.LocationChosenDelegate;
import com.fiuba.tdp.petadopt.model.Pet;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import android.support.v17.leanback.widget.HorizontalGridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PetDetailFragment extends Fragment {
    private Pet pet;
    private HorizontalGridView mHorizontalGridView;
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;


    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            mScrollState = newState;
        }
    };


    public PetDetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pet_detail, container, false);
        RecyclerView.Adapter adapter = new HorizontalGridViewAdapter(pet.getImages());
        mHorizontalGridView = (HorizontalGridView) rootView.findViewById(R.id.horizontal_gridView);
        mHorizontalGridView.setAdapter(adapter);
        mHorizontalGridView.setWindowAlignment(HorizontalGridView.WINDOW_ALIGN_BOTH_EDGE);
        mHorizontalGridView.setWindowAlignmentOffsetPercent(35);
        mHorizontalGridView.addOnScrollListener(mScrollListener);
        mHorizontalGridView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        Button showMapButton = (Button) rootView.findViewById(R.id.show_map_button);
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLocationMapFragment mapFragment = new ShowLocationMapFragment();
                mapFragment.setPetLocation(pet.getLocation());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, mapFragment, "Choose location");
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return rootView;
    }


    public void setPet(Pet pet) {
        this.pet = pet;
    }


    private class HorizontalGridViewAdapter extends RecyclerView.Adapter {
        private ArrayList<Pet.Image> images;

        public HorizontalGridViewAdapter(ArrayList<Pet.Image> images) {
            super();
            this.images = images;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ImageView imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(8, 8, 8, 8);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ImageView imageView = ((ImageView) viewHolder.itemView);
            Picasso.with(getContext())
                    .load(images.get(i).getOriginalUrl())
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon)
                    .into(imageView);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    private class ImageViewHolder extends ViewHolder {
        public ImageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
