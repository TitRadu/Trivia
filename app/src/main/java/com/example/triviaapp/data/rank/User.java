package com.example.triviaapp.data.rank;

public class User{
    private String email;
    private String password;
    private String userName;
    private String loginCode;
    private int points;
    private int superpower5050;
    private int superpowerCorrectAnswer;
    private int gamesWon;
    private long dailyQuestionTime;
    private long luckModeTime;

    public User(String email, int gamesWon , String password, int points, int superpower5050, int superpowerCorrectAnswer, String userName, String loginCode, long dailyQuestionTime, long luckModeTime) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.loginCode = loginCode;
        this.points = points;
        this.superpower5050 = superpower5050;
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
    public int getSuperpower5050() {
        return superpower5050;
    }

    public void setSuperpower5050(int superpower5050) {
        this.superpower5050 = superpower5050;
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

    public String getLoginCode() {
        return loginCode;
    }
}