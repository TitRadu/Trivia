package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    Button createAccountButton;
    TextView alreadyHaveAccountTextView;
    String emptyEmailToast, emptyUserNameToast, emptyPasswordToast, shortPasswordToast, existUserNameToast, successCreateToast, existEmailToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();
        languageChangeListener();

    }

    private void initializeViews(){
        userNameInput = findViewById(R.id.userNameRegInput);
        emailInput = findViewById(R.id.emailRegInput);
        passwordInput = findViewById(R.id.passwordRegInput);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseHelper = FirebaseHelper.getInstance();

        createAccountButton = findViewById(R.id.createAccountButton);
        alreadyHaveAccountTextView = findViewById(R.id.im_already_have_an_account);

        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        userNameInput.setHint(R.string.userNameHintRegEn);
        passwordInput.setHint(R.string.passwordHintLogRegEditEn);
        createAccountButton.setText(R.string.createButtonLogRegEn);
        alreadyHaveAccountTextView.setText(R.string.alreadyHaveTextViewRegEn);
        emptyEmailToast = getString(R.string.emptyMailToastLogRegEn);
        emptyUserNameToast = getString(R.string.emptyUserNameToastRegEn);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEditEn);
        shortPasswordToast = getString(R.string.shortPasswordToastRegEditEn);
        existUserNameToast = getString(R.string.existUserNameToastRegEditEn);
        successCreateToast = getString(R.string.successCreateToastRegEn);
        existEmailToast = getString(R.string.existEmailToastRegEn);

    }


    private void setViewForRomanianLanguage(){
        userNameInput.setHint(R.string.userNameHintRegRou);
        passwordInput.setHint(R.string.passwordHintLogRegEditRou);
        createAccountButton.setText(R.string.createButtonLogRegRou);
        alreadyHaveAccountTextView.setText(R.string.alreadyHaveTextViewRegRou);
        emptyEmailToast = getString(R.string.emptyMailToastLogRegRou);
        emptyUserNameToast = getString(R.string.emptyUserNameToastRegRou);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEditRou);
        shortPasswordToast = getString(R.string.shortPasswordToastRegEditRou);
        existUserNameToast = getString(R.string.existUserNameToastRegEditRou);
        successCreateToast = getString(R.string.successCreateToastRegRou);
        existEmailToast = getString(R.string.existEmailToastRegRou);

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
        LoggedUserData.language.observeForever(s -> { chooseLanguage(); });

    }

    private boolean inputCheck(String userName, String email, String password){
        if(email.isEmpty()){
            Toast.makeText(this,emptyEmailToast,Toast.LENGTH_SHORT).show();
            return false;

        }

        if(userName.isEmpty()){
            Toast.makeText(this,emptyUserNameToast,Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.isEmpty()){
            Toast.makeText(this,emptyPasswordToast,Toast.LENGTH_SHORT).show();
            return false;

        }

        if(password.length() < 6){
            Toast.makeText(this,shortPasswordToast,Toast.LENGTH_SHORT).show();
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

        if(LoggedUserData.userNameList.contains(userName)){
            Toast.makeText(getBaseContext(), existUserNameToast, Toast.LENGTH_SHORT).show();
            return;

        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            User registeredUser = new User(email, password, 0,0, userName);
                            firebaseHelper.userDatabaseReference.child(UUID.randomUUID().toString()).setValue(registeredUser);
                            Toast.makeText(getBaseContext(), successCreateToast, Toast.LENGTH_SHORT).show();
                            LoggedUserData.loggedUserPassword = password;
                            LoggedUserData.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), existEmailToast, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void hasAnAccount(View view){
        finishAndRemoveTask();

    }

}