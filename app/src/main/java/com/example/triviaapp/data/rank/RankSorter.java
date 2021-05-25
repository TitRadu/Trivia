package com.example.triviaapp.data.rank;

import java.util.Comparator;

public class RankSorter implements Comparator<User> {


    @Override
    public int compare(User o1, User o2) {
        return o2.getPoints() - o1.getPoints();
    }

}
