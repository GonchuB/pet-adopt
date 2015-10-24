package com.fiuba.tdp.petadopt.fragments.detail.questions;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.model.Answer;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by lucas on 10/24/15.
 */
public class AnswerQuestionFragment extends Fragment {

    private Fragment previousFragment;
    private String answerText;
    private QuestionAnsweredDelegate delegate;

    public AnswerQuestionFragment() {
        super();
    }

    private Pet pet;
    private Question question;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_ask_question, container, false);
        TextView answerTextView = (TextView) rootView.findViewById(R.id.question_text);
        answerTextView.setHint(R.string.answer_placeholder);

        Button answerQuestionButton = (Button) rootView.findViewById(R.id.ask_question);
        answerQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView answerTextView = (TextView) rootView.findViewById(R.id.question_text);
                answerText = answerTextView.getText().toString();

                if (answerTextView.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), R.string.must_write_answer, Toast.LENGTH_LONG).show();
                    return;
                }
                PetsClient.instance().answerQuestion(pet.getId(), String.valueOf(question.getId()), answerText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int code, Header[] headers, JSONObject response) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(answerTextView.getWindowToken(), 0);
                        createLocalAnswer();
                        if (delegate != null) {
                            delegate.questionWasAnswered(question);
                        }
                        Toast.makeText(getActivity(), R.string.answer_question_success, Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();
                        previousFragment.onStart();
                    }
                });
            }

            private void createLocalAnswer() {
                Answer answer = new Answer();
                answer.setText(answerText);
                answer.setCreated(new Date());
                question.setAnswer(answer);
            }
        });
        return rootView;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setPreviousFragment(Fragment previousFragment) {
        this.previousFragment = previousFragment;
    }

    public void setDelegate(QuestionAnsweredDelegate delegate) {
        this.delegate = delegate;
    }
}
