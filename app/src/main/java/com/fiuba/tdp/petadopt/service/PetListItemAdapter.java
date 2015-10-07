package com.fiuba.tdp.petadopt.service;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.squareup.picasso.Picasso;

import java.util.Date;
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
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView type = (TextView) rowView.findViewById(R.id.type);
        TextView gender = (TextView) rowView.findViewById(R.id.gender);
        TextView colors = (TextView) rowView.findViewById(R.id.colors);
        TextView ago = (TextView) rowView.findViewById(R.id.ago);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        name.setText(pets.get(position).getName());
        if (pets.get(position).getType() == Pet.Type.Cat) {
            type.setText(R.string.cat_string);
        } else {
            type.setText(R.string.dog_string);
        }
        colors.setText(pets.get(position).getColors());
        if (pets.get(position).getGender() == Pet.Gender.female) {
            gender.setText(R.string.female_string);
        } else {
            gender.setText(R.string.male_string);
        }
        colors.setText(pets.get(position).getColors());

        CharSequence agoTxt = DateUtils.getRelativeTimeSpanString(pets.get(position).getCreatedAt().getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS, 0);

        ago.setText(agoTxt);

        if (pets.get(position).getFirstImage() != null) {
            Picasso.with(context)
                    .load(pets.get(position).getFirstImage().getMediumUrl())
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.icon);
        }
        return rowView;
    }
}