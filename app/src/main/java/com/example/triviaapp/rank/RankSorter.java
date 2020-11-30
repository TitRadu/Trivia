package com.example.triviaapp.rank;

import java.util.Comparator;

public class RankSorter implements Comparator<Rank> {


    @Override
    public int compare(Rank o1, Rank o2) {
        return o2.getPoints() - o1.getPoints();
    }

}
