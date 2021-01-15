package com.example.triviaapp.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class EditDataActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    EditText newUserNameView, oldPasswordView, newPasswordView, passwordDeleteView;
    RadioGroup chooseLanguageRadioGroup;
    RadioButton engRadioButton, romRadioButton;
    Button confirmDeleteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        initializeViews();
        initializeRadioGroup();
        languageChangeListener();

    }

    private void initializeViews(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        newUserNameView = findViewById(R.id.newUserNameView);
        oldPasswordView = findViewById(R.id.oldPasswordView);
        newPasswordView = findViewById(R.id.newPasswordView);
        passwordDeleteView = findViewById(R.id.passwordDeleteView);
        chooseLanguageRadioGroup = findViewById(R.id.chooseLanguageRadioGroup);
        engRadioButton = findViewById(R.id.engLanguageRadioButton);
        romRadioButton = findViewById(R.id.romLanguageRadioButton);
        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);

    }

    private void initializeRadioGroup(){
        String language = LoggedUserData.language.getValue();

        switch (language){
            case "english":
                chooseLanguageRadioGroup.check(engRadioButton.getId());
                break;
            case "romanian":
                chooseLanguageRadioGroup.check(romRadioButton.getId());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + language);
        }

    }

    private void languageChangeListener(){
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        chooseLanguageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId){
                case R.id.engLanguageRadioButton:
                    LoggedUserData.language.setValue("english");
                    editor.putString("language","english");
                    editor.apply();
                    break;
                case R.id.romLanguageRadioButton:
                    LoggedUserData.language.setValue("romanian");
                    editor.putString("language","romanian");
                    editor.apply();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + checkedId);
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

    public void updateData(View view){
        String newUserName = newUserNameView.getText().toString();
        String oldPassword = oldPasswordView.getText().toString();
        String newPassword = newPasswordView.getText().toString();

        updateUserName(newUserName);
        updatePassword(oldPassword, newPassword);

    }

    private void updateUserName(String newUserName) {
        if(newUserName.isEmpty()){
            return;

        }

        if (LoggedUserData.userNameList.contains(newUserName)) {
            Toast.makeText(getBaseContext(), "Username exists!", Toast.LENGTH_SHORT).show();
            return;

        }
        LoggedUserData.loggedUserName = newUserName;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);
        Toast.makeText(getBaseContext(), "Username changed successfully!", Toast.LENGTH_SHORT).show();
        newUserNameView.getText().clear();

    }

    private void updatePassword(String oldPassword, String newPassword) {
        if(oldPassword.isEmpty()){
            return;

        }

        if (!oldPassword.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), "Actual password is incorrect!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (newPassword.length() < 6) {
            Toast.makeText(getBaseContext(), "Password must contain minimum 6 characters!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getBaseContext(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                clearPasswordInputs();
                            }else{
                                Toast.makeText(getBaseContext(), "Change failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Log.d("TAG","Re-authenticate error!");

                }
            }
        });
    }

    public void exit(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    public void deleteAccount(View view){
        if(passwordDeleteView.getVisibility() == View.GONE) {
            passwordDeleteView.setVisibility(View.VISIBLE);
            confirmDeleteButton.setVisibility(View.VISIBLE);
        }else{
            passwordDeleteView.setVisibility(View.GONE);
            confirmDeleteButton.setVisibility(View.GONE);
            passwordDeleteView.getText().clear();

        }

    }

    public void confirmDelete(View view){
        String password = passwordDeleteView.getText().toString();

        if(password.isEmpty()){
            Toast.makeText(getBaseContext(), "Introduce password!", Toast.LENGTH_SHORT).show();
            return;

        }

        if (!password.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), "Actual password is incorrect!", Toast.LENGTH_SHORT).show();
            return;

        }


        AuthCredential credential = EmailAuthProvider.getCredential(LoggedUserData.loggedUserEmail,LoggedUserData.loggedUserPassword);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).removeValue();
                                Toast.makeText(getBaseContext(), "User account deleted!", Toast.LENGTH_SHORT).show();
                                finishAndRemoveTask();
                            }else{
                                Toast.makeText(getBaseContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
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