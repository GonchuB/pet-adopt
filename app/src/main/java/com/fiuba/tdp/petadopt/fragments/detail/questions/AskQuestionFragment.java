package com.fiuba.tdp.petadopt.fragments.detail.questions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.activities.MainActivity;
import com.fiuba.tdp.petadopt.fragments.detail.PetDetailFragment;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.fiuba.tdp.petadopt.service.QAClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class AskQuestionFragment extends Fragment {

    private PetDetailFragment previous_fragment;
    private ListView lv;
    private Pet pet;
    private QAClient client;
    private String auth_token;


    public AskQuestionFragment(){

    }

    public void setPreviousFragment(PetDetailFragment previous_fragment) {
        this.previous_fragment = previous_fragment;
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

                final TextView questionTextView = (TextView) getActivity().findViewById(R.id.question_text);

                Question question =  new Question(questionTextView.getText().toString());

                if (questionTextView.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), R.string.must_write_question, Toast.LENGTH_LONG).show();
                    return;
                }
                client.postQuestion(pet.getId(), question, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, JSONObject response) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(questionTextView.getWindowToken(), 0);
                        Toast.makeText(getActivity(), R.string.add_question_success, Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();
                        previous_fragment.reload();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                            Toast.makeText(getActivity(), R.string.auth_error, Toast.LENGTH_LONG).show();
                            ((MainActivity) getActivity()).goBackToLogin();
                        } else {
                            Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_LONG).show();
                        }
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
