package com.example.triviaapp;

public class Question {
    private int questionID;
    private String question;

    public Question(int questionID, String question){
        this.questionID = questionID;
        this.question = question;
    }

    public Question(){}

    public String getQuestion() {
        return question;
    }
    public int getQuestionID(){
        return  questionID;
    }

}
