package com.example.triviaapp.game.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;
import com.example.triviaapp.rank.Rank;
import com.example.triviaapp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    Button signOutButton, changeUserNameButton;

    TextView emailView, userNameView;

    EditText changeUserNameView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(root);
        searchLoggedUser(LoggedUserConstants.loggedUserEmail);
        setOnClickListeners();

        return root;

    }

    private void initializeViews(View root){
        firebaseAuth = FirebaseAuth.getInstance();
        signOutButton = root.findViewById(R.id.signOutBtn);
        changeUserNameButton = root.findViewById(R.id.changeUserNameBtn);
        emailView = root.findViewById(R.id.emailView);
        userNameView = root.findViewById(R.id.userNameView);
        changeUserNameView = root.findViewById(R.id.changeUserNameView);

    }

    private void setOnClickListeners(){
        signOutButton.setOnClickListener((v) -> signOut());
        changeUserNameButton.setOnClickListener((v) -> updateUserName(changeUserNameView.getText().toString()));

    }

    private void signOut(){
        firebaseAuth.signOut();
        getActivity().finishAndRemoveTask();

    }
    private void searchLoggedUser(String email){
        FirebaseHelper.userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User user = dataSnapshot1.getValue(User.class);
                    if(user.getEmail().equals(email)){
                        LoggedUserConstants.loggedUserName = user.getUserName();
                        LoggedUserConstants.loggedUserPassword = user.getPassword();
                        LoggedUserConstants.loggedUserKey = dataSnapshot1.getKey();
                        emailView.setText("Signed as " + LoggedUserConstants.loggedUserEmail);
                        userNameView.setText(LoggedUserConstants.loggedUserName);

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
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Rank rank = dataSnapshot1.getValue(Rank.class);
                    if(rank.getUserName().equals(LoggedUserConstants.loggedUserName)){
                        LoggedUserConstants.loggedUserPlace = rank.getPlace();
                        LoggedUserConstants.loggedUserPoints = rank.getPoints();
                        LoggedUserConstants.loggedUserRankKey = dataSnapshot1.getKey();

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

    private void updateUserName(String newUserName){
        if(LoggedUserConstants.userNameList.contains(newUserName)){
            Toast.makeText(getActivity(), "User name exists!", Toast.LENGTH_SHORT).show();
            return;

        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("email",LoggedUserConstants.loggedUserEmail);
        map.put("password",LoggedUserConstants.loggedUserPassword);
        map.put("userName", newUserName );
        FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);

        map = new HashMap<>();
        map.put("place",LoggedUserConstants.loggedUserPlace);
        map.put("userName",newUserName);
        map.put("points", LoggedUserConstants.loggedUserPoints );
        FirebaseHelper.rankingDatabaseReference.child(LoggedUserConstants.loggedUserRankKey).setValue(map);

    }

}