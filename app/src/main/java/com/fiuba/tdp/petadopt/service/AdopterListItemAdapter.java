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
import com.fiuba.tdp.petadopt.model.Adopter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tomas on 9/28/15.
 */

public class AdopterListItemAdapter extends ArrayAdapter<Adopter> {

    private final Activity context;
    private final List<Adopter> adopters;

    public AdopterListItemAdapter(Activity context,
                                  List<Adopter> adopters) {
        super(context, R.layout.pet_list_item, adopters);
        this.context = context;
        this.adopters = adopters;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adopter_list_item, null, true);
        TextView full_name = (TextView) rowView.findViewById(R.id.full_name);
        TextView email = (TextView) rowView.findViewById(R.id.email);
        TextView phone = (TextView) rowView.findViewById(R.id.phone);
        TextView ago = (TextView) rowView.findViewById(R.id.ago);

        full_name.setText(adopters.get(position).getFullName());
        email.setText(adopters.get(position).getEmail());
        phone.setText(adopters.get(position).getPhone());

        CharSequence agoTxt = DateUtils.getRelativeTimeSpanString(adopters.get(position).getCreatedAt().getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS, 0);

        ago.setText(agoTxt);

        return rowView;
    }
}