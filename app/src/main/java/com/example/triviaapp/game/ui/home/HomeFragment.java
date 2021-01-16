package com.example.triviaapp.game.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.EditDataActivity;
import com.example.triviaapp.rank.User;
import com.example.triviaapp.rank.RankSorter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button editActivityButton, signOutButton;
    TextView userNameView, emailView, pointsView, placeView;
    String emailTextViewString, placeTextViewString, pointsTextViewString;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(root);
        searchLoggedUser(LoggedUserData.loggedUserEmail);
        setOnClickListeners();
        languageChangeListener();

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        userNameView.setText(LoggedUserData.loggedUserName);

    }

    private void initializeViews(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        emailView = root.findViewById(R.id.emailView);
        pointsView = root.findViewById(R.id.pointsView);
        placeView = root.findViewById(R.id.placeView);
        signOutButton = root.findViewById(R.id.btn_sign_out_profile);
        userNameView = root.findViewById(R.id.userNameView);
        editActivityButton = root.findViewById(R.id.btn_edit_data);

        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        emailTextViewString = getString(R.string.emailTextViewProfileEn);
        placeTextViewString = getString(R.string.placeTextViewProfileEn);
        pointsTextViewString = getString(R.string.pointsTextViewProfileEn);
        editActivityButton.setText(R.string.editButtonProfileEditEn);
        signOutButton.setText(R.string.signOutButtonProfileEn);

    }


    private void setViewForRomanianLanguage(){
        emailTextViewString = getString(R.string.emailTextViewProfileRou);
        placeTextViewString = getString(R.string.placeTextViewProfileRou);
        pointsTextViewString = getString(R.string.pointsTextViewProfileRou);
        editActivityButton.setText(R.string.editButtonProfileEditRou);
        signOutButton.setText(R.string.signOutButtonProfileRou);

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

    private void languageChangeListener(){
        LoggedUserData.language.observeForever(s -> {
            chooseLanguage();
            emailView.setText(emailTextViewString + LoggedUserData.loggedUserEmail);
            pointsView.setText(pointsTextViewString + LoggedUserData.loggedUserPoints);
            placeView.setText(placeTextViewString + LoggedUserData.loggedUserPlace);

        });

    }

    private HashMap<String, Object> populateMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", LoggedUserData.loggedUserEmail);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower",LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("userName", LoggedUserData.loggedUserName);
        return map;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickListeners() {
        signOutButton.setOnClickListener((v) -> signOut());
        editActivityButton.setOnClickListener((v) -> EditDataActivity());

    }

    private void searchLoggedUser(String email) {
        FirebaseHelper.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                LoggedUserData.ranksList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    LoggedUserData.ranksList.add(user);

                    if (user.getEmail().equals(email)) {
                        LoggedUserData.loggedUserName = user.getUserName();
                        LoggedUserData.loggedUserPoints = user.getPoints();
                        LoggedUserData.loggedUserKey = dataSnapshot1.getKey();
                        LoggedUserData.loggedSuperPowerFiftyFifty = user.getSuperpower();
                        Log.e("superpower",LoggedUserData.loggedSuperPowerFiftyFifty+"");

                        if(!LoggedUserData.loggedUserPasswordUpdateVerify)
                        if(!LoggedUserData.loggedUserPassword.equals("empty")) {
                            LoggedUserData.loggedUserPasswordUpdateVerify = true;
                            HashMap<String, Object> map = populateMap();
                            user.setPassword(LoggedUserData.loggedUserPassword);

                            FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

                        }

                        LoggedUserData.loggedUserPassword = user.getPassword();

                        emailView.setText(emailTextViewString + LoggedUserData.loggedUserEmail);
                        userNameView.setText(LoggedUserData.loggedUserName);
                        pointsView.setText(pointsTextViewString + LoggedUserData.loggedUserPoints);


                    }

                }

                Collections.sort(LoggedUserData.ranksList, new RankSorter());
                for (User rank1 : LoggedUserData.ranksList) {
                    if (rank1.getUserName().equals(LoggedUserData.loggedUserName)) {
                        LoggedUserData.loggedUserPlace = LoggedUserData.ranksList.indexOf(rank1) + 1;
                        placeView.setText(placeTextViewString + LoggedUserData.loggedUserPlace);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "User name not found!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void signOut() {
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        firebaseAuth.signOut();
        getActivity().finishAndRemoveTask();
    }

    private void EditDataActivity(){
        Intent intent = new Intent(getContext(), EditDataActivity.class);
        startActivity(intent);

    }

}