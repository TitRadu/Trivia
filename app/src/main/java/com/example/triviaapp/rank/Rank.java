package com.example.triviaapp.rank;

public class Rank {
    private int place;
    private String userName;
    private int points;

    public Rank(){}

    public Rank(Integer place, String userName, int points) {
        this.place = place;
        this.userName = userName;
        this.points = points;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
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
