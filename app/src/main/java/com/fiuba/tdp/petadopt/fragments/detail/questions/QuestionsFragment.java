package com.fiuba.tdp.petadopt.fragments.detail.questions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class QuestionsFragment extends Fragment {

    private ListView lv;
    private Pet pet;
    private List<Question> questions;
    private ProgressDialog progress;
    private QAListItemAdapter qaListItemAdapter;

    public QuestionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_questions, container, false);
        progress = new ProgressDialog(getActivity());
        progress.setTitle(R.string.loading);
        lv = (ListView) rootView.findViewById(R.id.question_list);

        progress.show();
        PetsClient.instance().getPet(pet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progress.dismiss();
                try {
                    pet.loadQuestionsFromJson(response);
                    if (pet.getQuestions() != null && pet.getQuestions().size() > 0) {
                        qaListItemAdapter = new QAListItemAdapter(getActivity(), pet.getQuestions());
                        getActivity().findViewById(R.id.no_results).setVisibility(View.INVISIBLE);
                    } else {
                        qaListItemAdapter = new QAListItemAdapter(getActivity(), new ArrayList<Question>());
                        getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
                    }
                    lv.setAdapter(qaListItemAdapter);
                    qaListItemAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("Error parsing pet", e.getLocalizedMessage());
                }
            }
        });

        return rootView;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
