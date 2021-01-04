package com.example.triviaapp.game.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.rank.User;
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

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView signOutButton;

    TextView emailView, pointsView, placeView,changePassword;

    EditText userNameView, oldPasswordView, newPasswordView;

    Button btn_save_user_profile;
    private boolean isUsernameModifyed, isPasswodModified;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(root);
        searchLoggedUser(LoggedUserData.loggedUserEmail);
        setOnClickListeners();

        return root;

    }
    public void setVisibilitySaveBtn(boolean isVisible){
        if(isVisible){
            btn_save_user_profile.setVisibility(View.VISIBLE);
        }else{
            btn_save_user_profile.setVisibility(View.GONE);
        }
    }
    public void setVisibilityTvPasswod(){
        oldPasswordView.setVisibility(View.VISIBLE);
        newPasswordView.setVisibility(View.VISIBLE);
    }
    private void initializeViews(View root) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        emailView = root.findViewById(R.id.emailView);
        pointsView = root.findViewById(R.id.pointsView);
        placeView = root.findViewById(R.id.placeView);
        signOutButton = root.findViewById(R.id.signOutBtn);
        userNameView = root.findViewById(R.id.userNameView);
        oldPasswordView = root.findViewById(R.id.oldPasswordView);
        newPasswordView = root.findViewById(R.id.newPasswordView);
        btn_save_user_profile=root.findViewById(R.id.btn_save_user_profile);
        changePassword=root.findViewById(R.id.tv_change_password);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickListeners() {
        signOutButton.setOnClickListener((v) -> signOut());
        userNameView.setOnTouchListener((v, event) -> {
            setVisibilitySaveBtn(true);
            isUsernameModifyed = true;
            return false;
        });
        changePassword.setOnClickListener(v -> {
            setVisibilityTvPasswod();
            setVisibilitySaveBtn(true);
            isPasswodModified = true;
        });
        btn_save_user_profile.setOnClickListener(v -> {
            saveData();
        });
    }

    private void saveData() {
        if(isPasswodModified){
            updatePassword(oldPasswordView.getText().toString(), newPasswordView.getText().toString());
        }
        if(isUsernameModifyed){
            updateUserName(userNameView.getText().toString());
        }
    }

    private void signOut() {
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        firebaseAuth.signOut();
        getActivity().finishAndRemoveTask();
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

                        if(!LoggedUserData.loggedUserPasswordUpdateVerify)
                        if(!LoggedUserData.loggedUserPassword.equals("empty")) {
                            LoggedUserData.loggedUserPasswordUpdateVerify = true;
                            HashMap<String, Object> map = populateMap();
                            user.setPassword(LoggedUserData.loggedUserPassword);

                            FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

                        }

                        LoggedUserData.loggedUserPassword = user.getPassword();

                        emailView.setText("Signed as " + LoggedUserData.loggedUserEmail);
                        userNameView.setText(LoggedUserData.loggedUserName);
                        pointsView.setText("Points:" + LoggedUserData.loggedUserPoints);


                    }

                }

                Collections.sort(LoggedUserData.ranksList, new RankSorter());
                for (User rank1 : LoggedUserData.ranksList) {
                    if (rank1.getUserName().equals(LoggedUserData.loggedUserName)) {
                        LoggedUserData.loggedUserPlace = LoggedUserData.ranksList.indexOf(rank1) + 1;
                        placeView.setText("Place:" + LoggedUserData.loggedUserPlace);

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

    private HashMap<String, Object> populateMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", LoggedUserData.loggedUserEmail);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        return map;
    }


    private void clearPasswordInputs(){
        oldPasswordView.getText().clear();
        newPasswordView.getText().clear();

    }

    private void updateUserName(String newUserName) {
        if (LoggedUserData.userNameList.contains(newUserName)) {
            Toast.makeText(getActivity(), "User name exists!", Toast.LENGTH_SHORT).show();
            return;

        }
        LoggedUserData.loggedUserName = newUserName;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

        userNameView.setText(LoggedUserData.loggedUserName);

    }

    private void updatePassword(String oldPassword, String newPassword) {
        if (!oldPassword.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getContext(), "Actual password is incorrect!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (newPassword.length() < 6) {
            Toast.makeText(getContext(), "Password must contain minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;

        }

        AuthCredential credential = EmailAuthProvider.getCredential(LoggedUserData.loggedUserEmail,LoggedUserData.loggedUserPassword);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                LoggedUserData.loggedUserPassword = newPassword;
                                HashMap<String, Object> map = populateMap();
                                FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);
                                Toast.makeText(getContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                clearPasswordInputs();
                            }else{
                                Toast.makeText(getContext(), "Change failed!", Toast.LENGTH_SHORT).show();
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