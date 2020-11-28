package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.triviaapp.game.GameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedUser;

    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeUserNameList();
        initializeLoggedUser();

    }

    private void initializeViews(){
        FirebaseHelper.getInstance();
        LoggedUserConstants.userNameList = new ArrayList<>();
        FirebaseHelper.getInstance();
        emailInput = findViewById(R.id.emailLogInput);
        passwordInput = findViewById(R.id.passwordLogInput);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void initializeLoggedUser(){
        loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        if(loggedUser != null){
            updateUI();

        }

    }

    private boolean inputCheck(String email, String password){
        if(email.isEmpty()){
            Toast.makeText(this,"Introduce a email!",Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.isEmpty()){
            Toast.makeText(this,"Introduce a password!",Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;

    }


    public void  registerActivity(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);

    }

    public void  logInActivity(View view){
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if(!inputCheck(email,password)){
            return;

        }
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getBaseContext(), "Succes!", Toast.LENGTH_SHORT).show();
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getBaseContext(), "Email or password incorrect!", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }

        private void updateUI(){
            LoggedUserConstants.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);

        }

    public void  exitApp(View view){
        finishAndRemoveTask();

    }

    private void initializeUserNameList(){
        FirebaseHelper.userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and NO again
                // whenever data at this location is updated.
                LoggedUserConstants.userNameList = new ArrayList<>();
                LoggedUserConstants.usersCount = 0;

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User user = dataSnapshot1.getValue(User.class);
                    LoggedUserConstants.userNameList.add(user.getUserName());
                    LoggedUserConstants.usersCount++;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "User name not found!", Toast.LENGTH_SHORT).show();

            }

        });

    }

}