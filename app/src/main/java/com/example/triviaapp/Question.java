package com.example.triviaapp;

public class Question {
    private int questionID;
    private String question;
    private String category;

    public Question(int questionID, String question, String category){
        this.questionID = questionID;
        this.question = question;
        this.category = category;
    }

    public Question(){}

    public String getQuestion() {
        return question;
    }
    public int getQuestionID(){
        return  questionID;
    }

    public String getCategory() {
        return category;
    }
}
