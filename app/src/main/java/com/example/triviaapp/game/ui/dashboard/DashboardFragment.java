package com.example.triviaapp.game.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
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

    TextView placeTextView, pointsTextView, userNameTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initializeViews(root);
        initializeRanksList();
        return root;
    }

    private void initializeViews(View root){
        rankListRV = root.findViewById(R.id.rv_rank_list);
        placeTextView = root.findViewById(R.id.placeTextView);
        pointsTextView = root.findViewById(R.id.pointsTextView);
        userNameTextView = root.findViewById(R.id.userNameTextView);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        placeTextView.setText(R.string.placeTextViewProfileEn);
        pointsTextView.setText(R.string.pointsTextViewProfileEn);
        userNameTextView.setText(R.string.userNameTextViewRankEn);

    }


    private void setViewForRomanianLanguage(){
        placeTextView.setText(R.string.placeTextViewProfileRou);
        pointsTextView.setText(R.string.pointsTextViewProfileRou);
        userNameTextView.setText(R.string.userNameTextViewRankRou);

    }

    private void chooseLanguage(){
        switch (LoggedUserData.language.getValue()){
            case "english":
                setViewForEnglishLanguage();
                break;
            case "romanian":
                setViewForRomanianLanguage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language.getValue());
        }

    }

    private void setRecyclerView(){
        rankAdapter = new RankAdapter(LoggedUserData.ranksList);
        rankListRV.setLayoutManager(new LinearLayoutManager(getContext()));
        rankListRV.setAdapter(rankAdapter);

    }

    private void initializeRanksList(){
        FirebaseHelper.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and NO again
                // whenever data at this location is updated.
                LoggedUserData.ranksList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User rank = dataSnapshot1.getValue(User.class);
                    LoggedUserData.ranksList.add(rank);

                }
                Collections.sort(LoggedUserData.ranksList,new RankSorter());
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