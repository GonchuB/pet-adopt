package com.fiuba.tdp.petadopt.fragments.detail;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fiuba.tdp.petadopt.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullScreenImageFragment extends Fragment {


    private PhotoViewAttacher mAttacher;
    private Drawable imageDrawable;

    public FullScreenImageFragment() {
        // Required empty public constructor
    }


    public void setImageDrawable(Drawable d){
        imageDrawable = d;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        imageView.setImageDrawable(imageDrawable);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(imageView);
        return rootView;
    }


}
