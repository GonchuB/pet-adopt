package com.fiuba.tdp.petadopt.fragments.detail.questions;

import com.fiuba.tdp.petadopt.model.Question;

public interface QuestionAnsweredDelegate {
    void questionWasAnswered(Question question);
}
