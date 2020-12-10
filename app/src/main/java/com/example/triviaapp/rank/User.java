package com.example.triviaapp.rank;

public class User{
    private String email;
    private String userName;
    private String password;
    private int points;

    public User(String email, String userName, String password, int points) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.points = points;
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

}