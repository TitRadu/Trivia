package com.example.triviaapp;

public class Answer {
    private  int answerId;
    private  String answer;
    private boolean isCorrect;
    private int questionId;

    public Answer(){}

    public Answer(int answerId, String answer, boolean isCorrect, int questionId) {
        this.answerId = answerId;
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public int getQuestionId() {
        return questionId;
    }
}
