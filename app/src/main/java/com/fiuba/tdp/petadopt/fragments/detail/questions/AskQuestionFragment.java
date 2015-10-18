package com.fiuba.tdp.petadopt.fragments.detail.questions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.fiuba.tdp.petadopt.service.QAClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class AskQuestionFragment extends Fragment {

    private ListView lv;
    private Pet pet;
    private QAClient client;
    private String auth_token;

    public AskQuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ask_question, container, false);

        auth_token = User.user().getAuthToken();
        client = QAClient.instance();
        client.setAuth_token(auth_token);


        Button showQuestionsButton = (Button) rootView.findViewById(R.id.ask_question);
        showQuestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView questionTextView = (TextView) getActivity().findViewById(R.id.question_text);

                String questionText = questionTextView.getText().toString();
                if (questionText.equals("")) {
                    Toast.makeText(getActivity(), R.string.must_write_question, Toast.LENGTH_LONG).show();
                    return;
                }
                client.postQuestion(pet.getId(), questionText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, JSONObject response) {
                        Toast.makeText(getActivity(), R.string.add_question_success, Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();
                    }
                });
            }
        });



        return rootView;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
