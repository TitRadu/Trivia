package com.example.triviaapp.rank;

public class Rank {
    private String userName;
    private int points;

    public Rank(){}

    public Rank(String userName, int points) {
        this.userName = userName;
        this.points = points;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
