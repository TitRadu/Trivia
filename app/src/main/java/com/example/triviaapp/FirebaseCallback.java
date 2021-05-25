package com.example.triviaapp;

import com.example.triviaapp.data.Answer;
import com.example.triviaapp.data.Question;

import java.util.List;

public interface FirebaseCallback {
    void onCallbackQuestions(List<Question> questions);
    void onCallbackAnswers(List<Answer> answers);

}
