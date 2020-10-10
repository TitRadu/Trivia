package com.example.triviaapp;

import java.util.List;

public interface FirebaseCallback {
    void onCallback(List<Question> questions);
    void onCallbackAnswers(List<Answer> answers);
}
