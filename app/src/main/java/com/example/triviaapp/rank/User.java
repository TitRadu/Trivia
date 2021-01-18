package com.example.triviaapp.rank;

public class User{
    private String email;
    private String password;
    private int points;
    private int superpower;
    private int superpowerCorrectAnswer;
    private String userName;

    public User(String email, String password, int points, int superpower,int superpowerCorrectAnswer,String userName) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.points = points;
        this.superpower = superpower;
        this.superpowerCorrectAnswer = superpowerCorrectAnswer;
    }

    public User(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() { return points; }

    public void setPoints(int points) { this.points = points; }
    public int getSuperpower() {
        return superpower;
    }

    public void setSuperpower(int superpower) {
        this.superpower = superpower;
    }

    public int getSuperpowerCorrectAnswer() {
        return superpowerCorrectAnswer;
    }

    public void setSuperpowerCorrectAnswer(int superpowerCorrectAnswer) {
        this.superpowerCorrectAnswer = superpowerCorrectAnswer;
    }
}