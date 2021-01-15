package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.game.GameActivity;
import com.example.triviaapp.game.ui.SubmitButton;
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

import static com.example.triviaapp.LoggedUserData.optionList;

public class MainActivity extends AppCompatActivity {
    public static final Integer RECORD_AUDIO = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedUser;

    private EditText emailInput, passwordInput, forgotPasswordEmailInput;

    private LinearLayout forgotPasswordLayout;

    Button createAccountButton, sendMailButton;
    TextView forgotPasswordTextView;
    String emptyMailToast, emptyPasswordToast, successDataToast, wrongDataToast, successSendMailToast, wrongMailToast, audioGrantedToast, audioDeniedToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeMicrophoneStatusAndCategoriesOptions();
        initializeViews();
        initializeUserNameList();
        initializeLoggedUser();
        verifyAudioPermission();
        languageChangeListener();


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

        createAccountButton = findViewById(R.id.createAccountButton);
        sendMailButton = findViewById(R.id.sendMailButton);
        forgotPasswordTextView = findViewById(R.id.tv_forgotPassword);

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

    private void setViewForEnglishLanguage(){
        passwordInput.setHint(R.string.passwordHintLogRegEditEn);
        createAccountButton.setText(R.string.createButtonLogRegEn);
        forgotPasswordTextView.setText(R.string.forgotPasswordTextViewLogEn);
        sendMailButton.setText(R.string.sendMailButtonLogEn);
        emptyMailToast = getString(R.string.emptyMailToastLogRegEn);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEn);
        successDataToast = getString(R.string.successDataToastLogEn);
        wrongDataToast = getString(R.string.wrongDataToastLogEn);
        successSendMailToast = getString(R.string.successSendMailToastLogEn);
        wrongMailToast = getString(R.string.wrongMailToastLogEn);
        audioGrantedToast = getString(R.string.audioGrantedToastLogEn);
        audioDeniedToast = getString(R.string.audioDeniedToastLogEn);

    }

    private void setViewForRomanianLanguage(){
        passwordInput.setHint(R.string.passwordHintLogRegEditRou);
        createAccountButton.setText(R.string.createButtonLogRegRou);
        forgotPasswordTextView.setText(R.string.forgotPasswordTextViewLogRou);
        sendMailButton.setText(R.string.sendMailButtonLogRou);
        emptyMailToast = getString(R.string.emptyMailToastLogRegRou);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegRou);
        successDataToast = getString(R.string.successDataToastLogRou);
        wrongDataToast = getString(R.string.wrongDataToastLogRou);
        successSendMailToast = getString(R.string.successSendMailToastLogRou);
        wrongMailToast = getString(R.string.wrongMailToastLogRou);
        audioGrantedToast = getString(R.string.audioGrantedToastLogRou);
        audioDeniedToast = getString(R.string.audioDeniedToastLogRou);

    }

    private void languageChangeListener(){

        LoggedUserData.language.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
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
        });

    }

    private void initializeLoggedUser(){
        loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        if(loggedUser != null){
            updateUI();

        }

    }

    private boolean inputCheck(String email, String password){
        if(email.isEmpty()){
            Toast.makeText(this,emptyMailToast,Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.isEmpty()){
            Toast.makeText(this,emptyPasswordToast,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getBaseContext(), successDataToast, Toast.LENGTH_SHORT).show();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), wrongDataToast, Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this,emptyMailToast,Toast.LENGTH_SHORT).show();
            return;

        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), successSendMailToast, Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), wrongMailToast, Toast.LENGTH_SHORT).show();

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
                Toast.makeText(this, audioGrantedToast, Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, audioDeniedToast, Toast.LENGTH_SHORT).show();

            }

        }

    }

    private void initializeOptionList(){
        optionList = new ArrayList<>();
        optionList.add(new Option("mic",true));
        optionList.add(new Option("sport",true));
        optionList.add(new Option("geography",true));
        optionList.add(new Option("maths",true));
        optionList.add(new Option("others",true));

    }

    private void initializeMicrophoneStatusAndCategoriesOptions(){
        initializeOptionList();

        SharedPreferences prefs = getSharedPreferences("preferences.txt", MODE_PRIVATE);
        String data;

        for(Option option : optionList){
            data = prefs.getString(option.getName(),"Key not found!");

            if(data.equals("Key not found!")){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(option.getName(),"true");
                editor.apply();
                option.setValue(true);

            }else {
                option.setValue(data.equals("true"));

            }

        }

        data = prefs.getString("language", "Key not found!");
        if(data.equals("Key not found!")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("language","english");
            LoggedUserData.language.setValue("english");
            editor.apply();

        }else{
            LoggedUserData.language.setValue(data);

        }

    }

}