package com.example.triviaapp.rank;

public class User{
    private String email;
    private String password;
    private int points;
    private int superpower;
    private int superpowerCorrectAnswer;
    private int gamesWon;
    private String userName;
    private long dailyQuestionTime;
    private long luckModeTime;

    public User(String email,int gamesWon ,String password, int points, int superpower,int superpowerCorrectAnswer,String userName, long dailyQuestionTime, long luckModeTime) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.points = points;
        this.superpower = superpower;
        this.superpowerCorrectAnswer = superpowerCorrectAnswer;
        this.gamesWon = gamesWon;
        this.dailyQuestionTime = dailyQuestionTime;
        this.luckModeTime = luckModeTime;

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

    public int getGamesWon() {
        return gamesWon;
    }

    public long getDailyQuestionTime() {
        return dailyQuestionTime;
    }

    public long getLuckModeTime() {
        return luckModeTime;
    }

    public void setDailyQuestionTime(long dailyQuestionTime) {
        this.dailyQuestionTime = dailyQuestionTime;
    }
}