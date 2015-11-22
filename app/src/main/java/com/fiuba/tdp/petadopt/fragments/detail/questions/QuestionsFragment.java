package com.fiuba.tdp.petadopt.fragments.detail.questions;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.activities.MainActivity;
import com.fiuba.tdp.petadopt.fragments.detail.PetDetailFragment;
import com.fiuba.tdp.petadopt.fragments.dialog.ConfirmDialogDelegate;
import com.fiuba.tdp.petadopt.fragments.dialog.ConfirmDialogFragment;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class QuestionsFragment extends Fragment implements QuestionAnsweredDelegate {

    private Pet pet;
    private QAListItemAdapter qaListItemAdapter;
    public PetDetailFragment petDetailFragment;
    private ProgressDialog progress;

    public QuestionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_questions, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.question_list);
        if (pet.getQuestions() != null && pet.getQuestions().size() > 0) {
            qaListItemAdapter = new QAListItemAdapter(getActivity(), pet.getQuestions());
            getActivity().findViewById(R.id.no_results).setVisibility(View.INVISIBLE);
        } else {
            qaListItemAdapter = new QAListItemAdapter(getActivity(), new ArrayList<Question>());
            getActivity().findViewById(R.id.no_results).setVisibility(View.VISIBLE);
        }
        qaListItemAdapter.setIsOwner(User.user().ownsPet(pet));
        qaListItemAdapter.setAnswerQuestionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnswerQuestionFragment answerQuestionFragment = new AnswerQuestionFragment();
                answerQuestionFragment.setPreviousFragment(QuestionsFragment.this);
                answerQuestionFragment.setQuestion(pet.getQuestions().get((Integer) v.getTag()));
                answerQuestionFragment.setPet(pet);
                answerQuestionFragment.setDelegate(QuestionsFragment.this);
                FragmentTransaction ft = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.content_frame, answerQuestionFragment, "AnswerQuestion");
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        listView.setAdapter(qaListItemAdapter);
        qaListItemAdapter.notifyDataSetChanged();
        qaListItemAdapter.setReportQuestionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Integer questionIndex = (Integer) v.getTag();
                ConfirmDialogFragment dialog = new ConfirmDialogFragment(getString(R.string.question_reporting_confirmation), new ConfirmDialogDelegate() {
                    @Override
                    public void onConfirm(DialogInterface dialog, int id) {
                        progress = new ProgressDialog(getActivity());
                        progress.setTitle(R.string.loading);
                        progress.show();

                        PetsClient.instance().reportQuestion(pet, pet.getQuestions().get(questionIndex), new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                progress.dismiss();
                                Toast.makeText(getActivity(), R.string.question_reported, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                    Toast.makeText(getActivity(), R.string.auth_error, Toast.LENGTH_LONG).show();
                                    ((MainActivity) getActivity()).goBackToLogin();
                                } else {
                                    Toast.makeText(getActivity(), R.string.question_reporting_error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onReject(DialogInterface dialog, int id) {
                        Log.d("QuestionReportingDialog", "Rejected");
                    }

                    @Override
                    public String getConfirmMessage() {
                        return getString(R.string.confirm_adoption_request_message);
                    }

                    @Override
                    public String getRejectMessage() {
                        return getString(R.string.reject_adoption_request_message);
                    }
                });
                dialog.show(getFragmentManager(), "QuestionReportingDialog");
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        qaListItemAdapter.notifyDataSetChanged();
    }


    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public void questionWasAnswered(Question question) {
        if (petDetailFragment != null) {
            petDetailFragment.reload();
        }
    }
}
