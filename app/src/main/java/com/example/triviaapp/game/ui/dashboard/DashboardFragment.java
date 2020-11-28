package com.example.triviaapp.game.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;
import com.example.triviaapp.rank.Rank;
import com.example.triviaapp.rank.RankAdapter;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private RecyclerView rankListRV;
    private RankAdapter rankAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initializeViews(root);
        setRecyclerView();
        return root;
    }

    private void initializeViews(View root){
        rankListRV = root.findViewById(R.id.rv_rank_list);

    }

    private void setRecyclerView(){
        rankAdapter = new RankAdapter(LoggedUserConstants.ranksList);
        rankListRV.setLayoutManager(new LinearLayoutManager(getContext()));
        rankListRV.setAdapter(rankAdapter);

    }

}