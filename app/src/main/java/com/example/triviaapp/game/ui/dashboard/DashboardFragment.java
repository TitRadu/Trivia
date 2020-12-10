package com.example.triviaapp.game.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;
import com.example.triviaapp.rank.RankAdapter;
import com.example.triviaapp.rank.RankSorter;
import com.example.triviaapp.rank.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class DashboardFragment extends Fragment {
    private RecyclerView rankListRV;
    private RankAdapter rankAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initializeViews(root);
        initializeRanksList();
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

    private void initializeRanksList(){
        FirebaseHelper.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and NO again
                // whenever data at this location is updated.
                LoggedUserConstants.ranksList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User rank = dataSnapshot1.getValue(User.class);
                    LoggedUserConstants.ranksList.add(rank);

                }
                Collections.sort(LoggedUserConstants.ranksList,new RankSorter());
                setRecyclerView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "User name not found!", Toast.LENGTH_SHORT).show();

            }

        });

    }

}