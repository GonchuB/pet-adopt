package com.fiuba.tdp.petadopt.service;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomas on 9/28/15.
 */

public class PetListItemAdapter extends ArrayAdapter<Pet> {

    private final Activity context;
    private final List<Pet> pets;

    public PetListItemAdapter(Activity context,
                              List<Pet> pets) {
        super(context, R.layout.pet_list_item, pets);
        this.context = context;
        this.pets = pets;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.pet_list_item, null, true);
        TextView line1 = (TextView) rowView.findViewById(R.id.line_1);
        TextView line2 = (TextView) rowView.findViewById(R.id.line_2);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        line1.setText(pets.get(position).toString());
        line2.setText(pets.get(position).getColors());

        if (pets.get(position).getFirstImage() != null) {
            new DownloadImageTask(imageView).execute(pets.get(position).getFirstImage().getThumbUrl());
        }
        //imageView.setImageResource(imageId[position]);
        return rowView;
    }
}