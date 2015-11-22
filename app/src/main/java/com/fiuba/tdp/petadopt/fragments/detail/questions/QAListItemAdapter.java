package com.fiuba.tdp.petadopt.fragments.detail.questions;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.dialog.ConfirmDialogDelegate;
import com.fiuba.tdp.petadopt.fragments.dialog.ConfirmDialogFragment;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.fiuba.tdp.petadopt.util.DateUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;


public class QAListItemAdapter extends ArrayAdapter<Question> {
    private final List<Question> questions;
    private final Activity context;
    private Boolean isOwner;
    private View.OnClickListener answerQuestionListener;
    private View.OnClickListener reportQuestionListener;
    private Button answerButton;

    public QAListItemAdapter(Activity context,
                             List<Question> questions) {
        super(context, R.layout.pet_list_item, questions);
        this.context = context;
        this.questions = questions;

    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.qa_list_item, null, true);

        TextView questionTextView = (TextView) rowView.findViewById(R.id.question);
        TextView questionDateTextView = (TextView) rowView.findViewById(R.id.question_date);
        TextView questionAskerTextView = (TextView) rowView.findViewById(R.id.asker_name);
        TextView answerTextView = (TextView) rowView.findViewById(R.id.answer);
        TextView answerDateTextView = (TextView) rowView.findViewById(R.id.answer_date);
        answerButton = (Button) rowView.findViewById(R.id.answer_question_button);
        setupReportButton(rowView, position);

        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.relative_layout);

        Question question = questions.get(position);
        questionTextView.setText(question.getText());
        questionDateTextView.setText(DateUtils.stringFromDateForQuestionList(question.getCreatedAt()));
        questionAskerTextView.setText(question.getAsker());

        if (question.getAnswer() == null) {
            relativeLayout.removeView(answerDateTextView);
            relativeLayout.removeView(answerTextView);
        } else {
            answerTextView.setText(question.getAnswer().getText());
            answerDateTextView.setText(DateUtils.stringFromDateForQuestionList(question.getAnswer().getCreatedAt()));
            relativeLayout.removeView(answerButton);
        }

        if (!isOwner) {
            relativeLayout.removeView(answerButton);
        } else {
            answerButton.setOnClickListener(answerQuestionListener);
            answerButton.setTag(position);
        }
        return rowView;
    }

    private void setupReportButton(View rowView, Integer position) {
        Button button = (Button) rowView.findViewById(R.id.report_button);
        button.setPaintFlags(button.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        button.setOnClickListener(reportQuestionListener);
        button.setTag(position);
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public void setAnswerQuestionListener(View.OnClickListener answerQuestionListener) {
        this.answerQuestionListener = answerQuestionListener;
    }

    public Button getAnswerButton() {
        return answerButton;
    }

    public void setReportQuestionListener(View.OnClickListener reportQuestionListener) {
        this.reportQuestionListener = reportQuestionListener;
    }
}

