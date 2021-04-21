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
import android.widget.TextView;
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
    EditText newUserNameEditView, oldPasswordEditView, newPasswordEditView, passwordDeleteView;
    RadioGroup chooseLanguageRadioGroup;
    RadioButton engRadioButton, romRadioButton;
    Button editButton, backButton, deleteButton, confirmDeleteButton;
    TextView newUserNameTextView, oldPasswordTextView, newPasswordTextView, chooseLanguageTextView;
    String existUserNameToast, successUserNameToast, wrongPasswordToast, shortPasswordToast, successPasswordToast, emptyPasswordToast, successDeleteToast;


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
        newUserNameEditView = findViewById(R.id.newUserNameEditView);
        oldPasswordEditView = findViewById(R.id.oldPasswordEditView);
        newPasswordEditView = findViewById(R.id.newPasswordEditView);
        passwordDeleteView = findViewById(R.id.passwordDeleteView);
        chooseLanguageRadioGroup = findViewById(R.id.chooseLanguageRadioGroup);
        engRadioButton = findViewById(R.id.engLanguageRadioButton);
        romRadioButton = findViewById(R.id.romLanguageRadioButton);
        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
        newUserNameTextView = findViewById(R.id.newUserNameTextView);
        oldPasswordTextView = findViewById(R.id.oldPasswordTextView);
        newPasswordTextView = findViewById(R.id.newPasswordTextView);
        chooseLanguageTextView = findViewById(R.id.chooseLanguageTextView);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.exitButton);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        newUserNameTextView.setText(R.string.userNameTextViewEditEn);
        oldPasswordTextView.setText(R.string.oldPasswordTextViewEditEn);
        newPasswordTextView.setText(R.string.newPasswordTextViewEditEn);
        chooseLanguageTextView.setText(R.string.chooseLanguageTextViewEditEn);
        editButton.setText(R.string.editButtonEditEn);
        backButton.setText(R.string.backButtonEditEn);
        deleteButton.setText(R.string.deleteAccountButtonEditEn);
        passwordDeleteView.setHint(R.string.passwordHintLogRegEditEn);
        confirmDeleteButton.setText(R.string.confirmButtonEditEn);
        existUserNameToast = getString(R.string.existUserNameToastRegEditEn);
        successUserNameToast = getString(R.string.successUserNameToastEditEn);
        wrongPasswordToast = getString(R.string.wrongPasswordToastEditEn);
        shortPasswordToast = getString(R.string.shortPasswordToastRegEditEn);
        successPasswordToast = getString(R.string.successPasswordToastEditEn);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEditEn);
        successDeleteToast = getString(R.string.successDeleteToastEditEn);

    }


    private void setViewForRomanianLanguage(){
        newUserNameTextView.setText(R.string.userNameTextViewEditRou);
        oldPasswordTextView.setText(R.string.oldPasswordTextViewEditRou);
        newPasswordTextView.setText(R.string.newPasswordTextViewEditRou);
        chooseLanguageTextView.setText(R.string.chooseLanguageTextViewEditRou);
        editButton.setText(R.string.editButtonEditRou);
        backButton.setText(R.string.backButtonEditRou);
        deleteButton.setText(R.string.deleteAccountButtonEditRou);
        passwordDeleteView.setHint(R.string.passwordHintLogRegEditRou);
        confirmDeleteButton.setText(R.string.confirmButtonEditRou);
        existUserNameToast = getString(R.string.existUserNameToastRegEditRou);
        successUserNameToast = getString(R.string.successUserNameToastEditRou);
        wrongPasswordToast = getString(R.string.wrongPasswordToastEditRou);
        shortPasswordToast = getString(R.string.shortPasswordToastRegEditRou);
        successPasswordToast = getString(R.string.successPasswordToastEditRou);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEditRou);
        successDeleteToast = getString(R.string.successDeleteToastEditRou);

    }

    private void chooseLanguage(){
        switch (LoggedUserData.language){
            case "english":
                setViewForEnglishLanguage();
                break;
            case "romanian":
                setViewForRomanianLanguage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
        }

    }


    private void initializeRadioGroup(){
        switch (LoggedUserData.language){
            case "english":
                chooseLanguageRadioGroup.check(engRadioButton.getId());
                break;
            case "romanian":
                chooseLanguageRadioGroup.check(romRadioButton.getId());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
        }

    }

    private void languageChangeListener(){
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        chooseLanguageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId){
                case R.id.engLanguageRadioButton:
                    setViewForEnglishLanguage();
                    LoggedUserData.language = "english";
                    editor.putString("language","english");
                    editor.apply();
                    break;
                case R.id.romLanguageRadioButton:
                    setViewForRomanianLanguage();
                    LoggedUserData.language = "romanian";
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
        map.put("gamesWon",LoggedUserData.loggedGamesWon);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower",LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("superpowerCorrectAnswer",LoggedUserData.loggedSuperPowerCorrectAnswer);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("dailyQuestionTime", LoggedUserData.loggedUserDailyQuestionTime);
        map.put("luckModeTime", LoggedUserData.loggedUserLuckModeTime);
        return map;
    }

    private void clearPasswordInputs(){
        oldPasswordEditView.getText().clear();
        newPasswordEditView.getText().clear();

    }

    public void updateData(View view){
        String newUserName = newUserNameEditView.getText().toString();
        String oldPassword = oldPasswordEditView.getText().toString();
        String newPassword = newPasswordEditView.getText().toString();

        updateUserName(newUserName);
        updatePassword(oldPassword, newPassword);

    }

    private void updateUserName(String newUserName) {
        if(newUserName.isEmpty()){
            return;

        }

        if (LoggedUserData.userNameList.contains(newUserName)) {
            Toast.makeText(getBaseContext(), existUserNameToast, Toast.LENGTH_SHORT).show();
            return;

        }
        LoggedUserData.loggedUserName = newUserName;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);
        Toast.makeText(getBaseContext(), successUserNameToast, Toast.LENGTH_SHORT).show();
        newUserNameEditView.getText().clear();

    }

    private void updatePassword(String oldPassword, String newPassword) {
        if(oldPassword.isEmpty()){
            return;

        }

        if (!oldPassword.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), wrongPasswordToast, Toast.LENGTH_SHORT).show();
            return;

        }

        if (newPassword.length() < 6) {
            Toast.makeText(getBaseContext(), shortPasswordToast, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getBaseContext(), successPasswordToast, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
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
            Toast.makeText(getBaseContext(), emptyPasswordToast, Toast.LENGTH_SHORT).show();
            return;

        }

        if (!password.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), wrongPasswordToast, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getBaseContext(), successDeleteToast, Toast.LENGTH_SHORT).show();
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