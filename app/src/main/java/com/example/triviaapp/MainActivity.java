package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.triviaapp.game.GameActivity;
import com.example.triviaapp.rank.User;
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
    public static final Integer RECORD_AUDIO = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedUser;

    private EditText emailInput, passwordInput, forgotPasswordEmailInput;

    private LinearLayout forgotPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeUserNameList();
        initializeMicrophoneStatus();
        initializeLoggedUser();
        verifyAudioPermission();


    }

    private void initializeViews(){
        FirebaseHelper.getInstance();
        LoggedUserData.userNameList = new ArrayList<>();
        FirebaseHelper.getInstance();
        emailInput = findViewById(R.id.emailLogInput);
        passwordInput = findViewById(R.id.passwordLogInput);
        forgotPasswordEmailInput = findViewById(R.id.forgotPasswordEmailInput);
        forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout);
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
            Toast.makeText(this,"Introduce an email!",Toast.LENGTH_SHORT).show();
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
                                LoggedUserData.loggedUserPassword = password;
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
            LoggedUserData.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);

        }

    private void initializeUserNameList(){
        FirebaseHelper.userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and NO again
                // whenever data at this location is updated.
                LoggedUserData.userNameList = new ArrayList<>();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    User user = dataSnapshot1.getValue(User.class);
                    LoggedUserData.userNameList.add(user.getUserName());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "User name not found!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    public void changeForgotLayoutVisibility(View view){
        if(forgotPasswordLayout.getVisibility() == View.GONE){
            forgotPasswordLayout.setVisibility(View.VISIBLE);
        }else{
            forgotPasswordLayout.setVisibility(View.GONE);
            forgotPasswordEmailInput.getText().clear();

        }

    }

    public void sendEmail(View view){
        String email = forgotPasswordEmailInput.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(this,"Introduce an email!",Toast.LENGTH_SHORT).show();
            return;

        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "A reset-password mail was send!", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), "Incorrect mail!", Toast.LENGTH_SHORT).show();

                }

            }

        });

    }

    private void checkPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO);

    }

    private void verifyAudioPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RECORD_AUDIO && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Audio record granted!", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "Audio record denied!", Toast.LENGTH_SHORT).show();

            }

        }

    }

    private void initializeMicrophoneStatus(){
        String key = "mic";
        SharedPreferences prefs = getSharedPreferences("preferences.txt", MODE_PRIVATE);
        String data = prefs.getString(key,"Key not found!");
        if(data.equals("Key not found!")){
            data = "true";
            SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            editor.putString(key,data);
            editor.apply();
            LoggedUserData.userMicrophone = true;

        }else {
            LoggedUserData.userMicrophone = data.equals("true");

        }

    }

}