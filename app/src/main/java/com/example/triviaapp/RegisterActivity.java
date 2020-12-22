package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.triviaapp.game.GameActivity;
import com.example.triviaapp.rank.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseHelper firebaseHelper;

    private EditText userNameInput, emailInput, passwordInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();

    }

    private void initializeViews(){
        userNameInput = findViewById(R.id.userNameRegInput);
        emailInput = findViewById(R.id.emailRegInput);
        passwordInput = findViewById(R.id.passwordRegInput);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseHelper = FirebaseHelper.getInstance();

    }

    private boolean inputCheck(String userName, String email, String password){
        if(userName.isEmpty()){
            Toast.makeText(this,"Introduce a user name!",Toast.LENGTH_SHORT).show();
            return false;

        }


        if(email.isEmpty()){
            Toast.makeText(this,"Introduce a email!",Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.isEmpty()){
            Toast.makeText(this,"Introduce a password!",Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.length() < 6){
            Toast.makeText(this,"Password must contain minimum 6 characters!",Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;
    }

    public void createAccount(View view){
        final String userName = userNameInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if(!inputCheck(userName,email,password)){
            return;

        }

        if(LoggedUserConstants.userNameList.contains(userName)){
            Toast.makeText(getBaseContext(), "User name exists!", Toast.LENGTH_SHORT).show();
            return;

        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            User registeredUser = new User(email, userName, password, 0);
                            firebaseHelper.userDatabaseReference.child(UUID.randomUUID().toString()).setValue(registeredUser);
                            Toast.makeText(getBaseContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                            LoggedUserConstants.loggedUserPassword = password;
                            LoggedUserConstants.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), "Failed to create account!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void hasAnAccount(View view){
        finishAndRemoveTask();

    }

}