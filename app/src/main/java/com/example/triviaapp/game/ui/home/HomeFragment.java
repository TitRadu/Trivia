package com.example.triviaapp.game.ui.home;

import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;
import com.example.triviaapp.rank.Rank;
import com.example.triviaapp.User;
import com.example.triviaapp.rank.RankSorter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button signOutButton, changeUserNameShowButton,  changeUserNameButton, cancelChangeUserNameButton, changePasswordShowButton, changePasswordButton, cancelChangePasswordButton;

    TextView emailView, userNameView, pointsView, placeView;

    EditText changeUserNameView, oldPasswordView, newPasswordView;

    LinearLayout userNameLayout, passwordLayout;

    List<Rank> ranksLocalList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(root);
        searchLoggedUser(LoggedUserConstants.loggedUserEmail);
        setOnClickListeners();

        return root;

    }

    private void initializeViews(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        emailView = root.findViewById(R.id.emailView);
        pointsView = root.findViewById(R.id.pointsView);
        placeView = root.findViewById(R.id.placeView);
        signOutButton = root.findViewById(R.id.signOutBtn);
        userNameView = root.findViewById(R.id.userNameView);
        userNameLayout = root.findViewById(R.id.userNameLayout);
        changeUserNameView = root.findViewById(R.id.changeUserNameView);
        changeUserNameButton = root.findViewById(R.id.changeUserNameBtn);
        changeUserNameShowButton = root.findViewById(R.id.changeUserNameShowBtn);
        cancelChangeUserNameButton = root.findViewById(R.id.cancelChangeUserNameBtn);
        passwordLayout = root.findViewById(R.id.passwordLayout);
        oldPasswordView = root.findViewById(R.id.oldPasswordView);
        newPasswordView = root.findViewById(R.id.newPasswordView);
        changePasswordButton = root.findViewById(R.id.changePasswordBtn);
        changePasswordShowButton = root.findViewById(R.id.changePasswordShowBtn);
        cancelChangePasswordButton = root.findViewById(R.id.cancelChangePasswordBtn);

    }

    private void setOnClickListeners() {
        signOutButton.setOnClickListener((v) -> signOut());
        changeUserNameButton.setOnClickListener((v) -> updateUserName(changeUserNameView.getText().toString()));
        changeUserNameShowButton.setOnClickListener((v) -> userNameLayout.setVisibility(View.VISIBLE));
        cancelChangeUserNameButton.setOnClickListener((v) -> clearUserNameInput());
        changePasswordButton.setOnClickListener((v) -> updatePassword(oldPasswordView.getText().toString(), newPasswordView.getText().toString()));
        changePasswordShowButton.setOnClickListener((v) -> passwordLayout.setVisibility(View.VISIBLE));
        cancelChangePasswordButton.setOnClickListener((v) -> clearPasswordInputs());

    }

    private void signOut() {
        LoggedUserConstants.loggedUserPasswordUpdateVerify = false;
        firebaseAuth.signOut();
        getActivity().finishAndRemoveTask();

    }

    private void searchLoggedUser(String email) {
        FirebaseHelper.userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    if (user.getEmail().equals(email)) {
                        LoggedUserConstants.loggedUserName = user.getUserName();
                        LoggedUserConstants.loggedUserKey = dataSnapshot1.getKey();

                        if(!LoggedUserConstants.loggedUserPasswordUpdateVerify)//daca nu pun asta si fac change la parola se face update in FB la infinit.
                        if(!LoggedUserConstants.loggedUserPassword.equals("empty")) {
                            LoggedUserConstants.loggedUserPasswordUpdateVerify = true;
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("email", LoggedUserConstants.loggedUserEmail);
                            map.put("password", LoggedUserConstants.loggedUserPassword);
                            map.put("userName", LoggedUserConstants.loggedUserName);
                            FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);

                        }

                        LoggedUserConstants.loggedUserPassword = user.getPassword();
                        emailView.setText("Signed as " + LoggedUserConstants.loggedUserEmail);
                        userNameView.setText("User name:" + LoggedUserConstants.loggedUserName);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getContext(), "User name not found!", Toast.LENGTH_SHORT).show();

            }

        });

        FirebaseHelper.rankingDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                ranksLocalList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Rank rank = dataSnapshot1.getValue(Rank.class);
                    ranksLocalList.add(rank);
                    if (rank.getUserName().equals(LoggedUserConstants.loggedUserName)) {
                        LoggedUserConstants.loggedUserPoints = rank.getPoints();
                        LoggedUserConstants.loggedUserRankKey = dataSnapshot1.getKey();
                        pointsView.setText("Points:" + LoggedUserConstants.loggedUserPoints);

                    }

                }

                Collections.sort(ranksLocalList, new RankSorter());
                for (Rank rank1 : ranksLocalList) {
                    if (rank1.getUserName().equals(LoggedUserConstants.loggedUserName)) {
                        LoggedUserConstants.loggedUserPlace = ranksLocalList.indexOf(rank1) + 1;
                        placeView.setText("Place:" + LoggedUserConstants.loggedUserPlace);

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

    private void clearUserNameInput(){
        userNameLayout.setVisibility(View.GONE);
        changeUserNameView.getText().clear();

    }

    private void clearPasswordInputs(){
        passwordLayout.setVisibility(View.GONE);
        oldPasswordView.getText().clear();
        newPasswordView.getText().clear();

    }

    private void updateUserName(String newUserName) {
        if (LoggedUserConstants.userNameList.contains(newUserName)) {
            Toast.makeText(getActivity(), "User name exists!", Toast.LENGTH_SHORT).show();
            return;

        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("email", LoggedUserConstants.loggedUserEmail);
        map.put("password", LoggedUserConstants.loggedUserPassword);
        map.put("userName", newUserName);
        FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);

        map = new HashMap<>();
        map.put("userName", newUserName);
        map.put("points", LoggedUserConstants.loggedUserPoints);
        FirebaseHelper.rankingDatabaseReference.child(LoggedUserConstants.loggedUserRankKey).setValue(map);

        clearUserNameInput();

    }

    private void updatePassword(String oldPassword, String newPassword) {
        if (!oldPassword.equals(LoggedUserConstants.loggedUserPassword)) {
            Toast.makeText(getContext(), "Actual password is incorrect!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (newPassword.length() < 6) {
            Toast.makeText(getContext(), "Password must contain minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;

        }

        AuthCredential credential = EmailAuthProvider.getCredential(LoggedUserConstants.loggedUserEmail,LoggedUserConstants.loggedUserPassword);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("email", LoggedUserConstants.loggedUserEmail);
                                map.put("password", newPassword);
                                map.put("userName", LoggedUserConstants.loggedUserName);
                                FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);
                                Toast.makeText(getContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                clearPasswordInputs();

                            }else{
                                Toast.makeText(getContext(), "Change failed!", Toast.LENGTH_SHORT).show();
                                return;

                            }

                        }
                    });

                }else{
                    Log.d("TAG","Re-authenticate error!");

                }


            }
        });

    }

}