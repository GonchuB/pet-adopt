package com.fiuba.tdp.petadopt.fragments.detail;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.detail.questions.AskQuestionFragment;
import com.fiuba.tdp.petadopt.fragments.detail.questions.QuestionsFragment;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.Question;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.fiuba.tdp.petadopt.util.DateUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rey.material.widget.EditText;
import com.rey.material.widget.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PetDetailFragment extends Fragment {
    private Pet pet;
    private HorizontalGridView mHorizontalGridView;
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private ProgressDialog progress;
    FloatingActionButton floatingActionButton;
    Button askQuestionButton;


    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            mScrollState = newState;
        }
    };


    public PetDetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_pet_detail, container, false);
        RecyclerView.Adapter adapter = new HorizontalGridViewAdapter(pet.getImages());
        mHorizontalGridView = (HorizontalGridView) rootView.findViewById(R.id.horizontal_gridView);
        mHorizontalGridView.setAdapter(adapter);
        mHorizontalGridView.setWindowAlignment(HorizontalGridView.WINDOW_ALIGN_BOTH_EDGE);
        mHorizontalGridView.setWindowAlignmentOffsetPercent(35);
        mHorizontalGridView.addOnScrollListener(mScrollListener);
        mHorizontalGridView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        askQuestionButton = (Button) rootView.findViewById(R.id.ask_question);
        floatingActionButton  = (FloatingActionButton) rootView.findViewById(R.id.adopt_pet);
        if ((pet != null) && (pet.getUserId().equals(String.valueOf(User.user().getId())))) {
            floatingActionButton.setVisibility(View.GONE);
            askQuestionButton.setVisibility(View.GONE);
        }
        setAdoptionButton();

        setupTextViews(rootView);

                Button showMapButton = (Button) rootView.findViewById(R.id.show_map_button);
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLocationMapFragment mapFragment = new ShowLocationMapFragment();
                mapFragment.setPetLocation(pet.getLocation());
                FragmentTransaction ft = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.content_frame, mapFragment, "Questions");
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        progress = new ProgressDialog(getActivity());
        progress.setTitle(R.string.loading);
        progress.show();

        PetsClient.instance().getQuestions(pet.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progress.dismiss();
                try {
                    pet.loadQuestionsFromJson(response);
                    setupSampleQuestion(rootView);
                    askQuestionButton = (Button) rootView.findViewById(R.id.ask_question);
                    askQuestionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AskQuestionFragment askQuestionFragment = new AskQuestionFragment();
                            askQuestionFragment.setPet(pet);
                            FragmentTransaction ft = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.add(R.id.content_frame, askQuestionFragment, "Choose location");
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                } catch (JSONException e) {
                    Log.e("Error parsing pet", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progress.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                progress.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progress.dismiss();
            }
        });
        return rootView;
    }

    private void setAdoptionButton() {
        floatingActionButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.user().missingInfo()) {
                    Toast.makeText(getActivity(),R.string.user_missing_info,Toast.LENGTH_LONG).show();
                } else {
                    PetsClient client = PetsClient.instance();
                    progress.show();
                    client.askForAdoption(pet.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            progress.dismiss();
                            Toast.makeText(getActivity(),R.string.ask_for_adoption_success,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(), R.string.ask_for_adoption_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void setupSampleQuestion(View rootView) {
        //FIXME - Copypasted from Adapter, see if we can join both pieces
        RelativeLayout questionLayout = (RelativeLayout) rootView.findViewById(R.id.question_layout);
        if (pet.getQuestions() != null && pet.getQuestions().size() > 0) {
            TextView questionTextView = (TextView) questionLayout.findViewById(R.id.question);
            TextView questionDateTextView = (TextView) questionLayout.findViewById(R.id.question_date);
            TextView questionAskerTextView = (TextView) questionLayout.findViewById(R.id.asker_name);
            TextView answerTextView = (TextView) questionLayout.findViewById(R.id.answer);
            TextView answerDateTextView = (TextView) questionLayout.findViewById(R.id.answer_date);
            Button answerButton = (Button) questionLayout.findViewById(R.id.answer_question_button);

            Question question = pet.getQuestions().get(0);
            questionTextView.setText(question.getText());
            questionDateTextView.setText(DateUtils.stringFromDateForQuestionList(question.getCreatedAt()));
            questionAskerTextView.setText(question.getAsker());

            if (question.getAnswer() == null) {
                questionLayout.removeView(answerDateTextView);
                questionLayout.removeView(answerTextView);
            } else {
                answerTextView.setText(question.getAnswer().getText());
                answerDateTextView.setText(DateUtils.stringFromDateForQuestionList(question.getAnswer().getCreatedAt()));
            }
            questionLayout.removeView(answerButton);

            Button showQuestionsButton = (Button) rootView.findViewById(R.id.show_questions);
            showQuestionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuestionsFragment questionsFragment = new QuestionsFragment();
                    questionsFragment.setPet(pet);
                    FragmentTransaction ft = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.add(R.id.content_frame, questionsFragment, "Choose location");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });


        } else {
            RelativeLayout sampleQuestionLayout = (RelativeLayout) rootView.findViewById(R.id.sample_question_layout);
            RelativeLayout rootLayout = (RelativeLayout) rootView.findViewById(R.id.root_layout);
            rootLayout.removeView(sampleQuestionLayout);
        }
    }

    private void setupTextViews(View rootView) {
        EditText nameField = (EditText) rootView.findViewById(R.id.name);
        setPetField(nameField, pet.getName());

        EditText typeField = (EditText) rootView.findViewById(R.id.type);
        setPetField(typeField, pet.getTypeString());

        EditText ageField = (EditText) rootView.findViewById(R.id.age);
        setPetField(ageField, pet.getAge());

        EditText genderField = (EditText) rootView.findViewById(R.id.gender);
        setPetField(genderField, pet.getGenderString());

        EditText colorsField = (EditText) rootView.findViewById(R.id.colors);
        setPetField(colorsField, pet.getColors());

        EditText descriptionField = (EditText) rootView.findViewById(R.id.description);
        setPetField(descriptionField, pet.getDescription());

        EditText videoField = (EditText) rootView.findViewById(R.id.videos);
        setVideoField(videoField, pet.getVideos());

        EditText vaccinesField = (EditText) rootView.findViewById(R.id.vaccines);
        setBooleanField(vaccinesField, pet.getVaccinated());

        EditText petsRelationshipField = (EditText) rootView.findViewById(R.id.pets_relationship);
        setBooleanField(petsRelationshipField, pet.getPetFriendly());

        EditText kidsRelationshipField = (EditText) rootView.findViewById(R.id.kids_relationship);
        setBooleanField(kidsRelationshipField, pet.getChildrenFriendly());

        EditText transitHomeField = (EditText) rootView.findViewById(R.id.transit);
        setBooleanField(transitHomeField, pet.getNeedsTransitHome());
    }

    private void setBooleanField(EditText field, Boolean condition) {
        if (condition) {
            field.setText(R.string.true_value);
        } else {
            field.setText(R.string.false_value);
        }
        field.setKeyListener(null);

    }

    private void setVideoField(EditText videoField, ArrayList<String> videos) {
        if (videos.size() == 0) {
            videoField.setVisibility(View.GONE);
        } else {
            String videoString = "";
            for (int i = 0; i < videos.size(); i++) {
                if (!videoString.isEmpty()) {
                    videoString += "\n";
                }
                videoString += videos.get(i);
            }
            videoField.setText(videoString);
        }
        videoField.setKeyListener(null);
    }

    private void setPetField(EditText field, String text) {
        if (text.isEmpty()) {
            field.setVisibility(View.GONE);
        } else {
            field.setText(text);
        }
        field.setKeyListener(null);
    }


    public void setPet(Pet pet) {
        this.pet = pet;
        if (floatingActionButton != null && askQuestionButton != null) {
            if ((pet != null) && (pet.getUserId().equals(String.valueOf(User.user().getId())))) {
                floatingActionButton.setVisibility(View.GONE);
                askQuestionButton.setVisibility(View.GONE);
            }
        }
    }


    private class HorizontalGridViewAdapter extends RecyclerView.Adapter {
        private ArrayList<Pet.Image> images;

        public HorizontalGridViewAdapter(ArrayList<Pet.Image> images) {
            super();
            this.images = images;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ImageView imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FullScreenImageFragment fullScreenImageFragment = new FullScreenImageFragment();
                    ImageView view = (ImageView)v;
                    fullScreenImageFragment.setImageDrawable(view.getDrawable());
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.add(R.id.content_frame, fullScreenImageFragment, "Image fullscreen");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ImageView imageView = ((ImageView) viewHolder.itemView);
            Picasso.with(getContext())
                    .load(images.get(i).getOriginalUrl())
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon)
                    .into(imageView);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    private class ImageViewHolder extends ViewHolder {
        public ImageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
