package com.example.triviaapp.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
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
import com.example.triviaapp.RegisterActivity;
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
import java.util.HashMap;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.LoggedUserData.EXMIC;
import static com.example.triviaapp.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.LoggedUserData.connectionStatus;
import static com.example.triviaapp.LoggedUserData.currentActivity;
import static com.example.triviaapp.LoggedUserData.optionList;

public class EditDataActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    EditText newUserNameEditView, oldPasswordEditView, newPasswordEditView, passwordDeleteView;
    RadioGroup chooseLanguageRadioGroup;
    RadioButton engRadioButton, romRadioButton;
    Button editButton, backButton, deleteButton, confirmDeleteButton;
    TextView newUserNameTextView, oldPasswordTextView, newPasswordTextView, chooseLanguageTextView;
    String existUserNameToast, successUserNameToast, wrongPasswordToast, shortPasswordToast, successPasswordToast, emptyPasswordToast, successDeleteToast;

    private TextToSpeech textToSpeech;
    Locale selectedLanguage;

    String voiceInput = null;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;

    private boolean connectionListenerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        initialize();
        initializeViews();
        initializeRadioGroup();
        languageChangeListener();

    }

    private void initialize() {
        currentActivity = this;
        speechInitialize();
        setTextToSpeechListener();

    }

    private void initializeViews() {
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

    private void setViewForEnglishLanguage() {
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


    private void setViewForRomanianLanguage() {
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

    private void chooseLanguage() {
        switch (LoggedUserData.language) {
            case "english":
                setViewForEnglishLanguage();
                selectedLanguage = Locale.ENGLISH;
                break;
            case "romanian":
                setViewForRomanianLanguage();
                selectedLanguage = Locale.ENGLISH;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
        }

    }


    private void initializeRadioGroup() {
        switch (LoggedUserData.language) {
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

    private void checkOptions(String feedback) {
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(feedback, QUEUE_ADD);

        } else {
            if (optionList.get(EXMIC).isValue()) {
                getSpeechInput();

            }

        }

    }

    private void languageChangeListener() {
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        chooseLanguageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.engLanguageRadioButton:
                    setViewForEnglishLanguage();
                    checkOptions("English language was selected!");

                    /*selectedLanguage = Locale.ENGLISH;
                    speechInitialize();
                    setTextToSpeechListener("English language was selected!");
                    */


                    LoggedUserData.language = "english";
                    editor.putString("language", "english");
                    editor.apply();
                    break;
                case R.id.romLanguageRadioButton:
                    setViewForRomanianLanguage();
                    checkOptions("Romanian language was selected!");
                    LoggedUserData.language = "romanian";
                    editor.putString("language", "romanian");
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
        map.put("gamesWon", LoggedUserData.loggedGamesWon);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower", LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("superpowerCorrectAnswer", LoggedUserData.loggedSuperPowerCorrectAnswer);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("dailyQuestionTime", LoggedUserData.loggedUserDailyQuestionTime);
        map.put("luckModeTime", LoggedUserData.loggedUserLuckModeTime);
        return map;
    }

    private void clearPasswordInputs() {
        oldPasswordEditView.getText().clear();
        newPasswordEditView.getText().clear();

    }

    public void updateData(View view) {
        String newUserName = newUserNameEditView.getText().toString();
        String oldPassword = oldPasswordEditView.getText().toString();
        String newPassword = newPasswordEditView.getText().toString();

        updateUserName(newUserName);
        updatePassword(oldPassword, newPassword);

    }

    private void updateUserName(String newUserName) {
        if (newUserName.isEmpty()) {
            return;

        }

        if (LoggedUserData.userNameList.contains(newUserName)) {
            Toast.makeText(getBaseContext(), existUserNameToast, Toast.LENGTH_SHORT).show();
            checkOptions("Username exists!");
            return;

        }
        LoggedUserData.loggedUserName = newUserName;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);
        Toast.makeText(getBaseContext(), successUserNameToast, Toast.LENGTH_SHORT).show();
        newUserNameEditView.getText().clear();
        checkOptions("Username was set to " + newUserName + "!");

    }

    private void updatePassword(String oldPassword, String newPassword) {
        if (oldPassword.isEmpty()) {
            return;

        }

        if (!oldPassword.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), wrongPasswordToast, Toast.LENGTH_SHORT).show();
            checkOptions("Actual password is incorrect!");
            return;

        }

        if (newPassword.length() < 6) {
            Toast.makeText(getBaseContext(), shortPasswordToast, Toast.LENGTH_SHORT).show();
            checkOptions("Password must contain minimum 6 characters!");
            return;

        }

        AuthCredential credential = EmailAuthProvider.getCredential(LoggedUserData.loggedUserEmail, LoggedUserData.loggedUserPassword);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                LoggedUserData.loggedUserPassword = newPassword;
                                HashMap<String, Object> map = populateMap();
                                FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);
                                Toast.makeText(getBaseContext(), successPasswordToast, Toast.LENGTH_SHORT).show();
                                clearPasswordInputs();
                                checkOptions("Password was changed successfully!");
                            } else {
                                Toast.makeText(getBaseContext(), "Change failed!", Toast.LENGTH_SHORT).show();

                            }
                        }

                    });

                } else {
                    Log.d("TAG", "Re-authenticate error!");

                }

            }

        });

    }

    public void exit(View view) {
        speechRecognizer.destroy();
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    public void deleteAccount(View view) {
        if (passwordDeleteView.getVisibility() == View.GONE) {
            passwordDeleteView.setVisibility(View.VISIBLE);
            confirmDeleteButton.setVisibility(View.VISIBLE);
        } else {
            passwordDeleteView.setVisibility(View.GONE);
            confirmDeleteButton.setVisibility(View.GONE);
            passwordDeleteView.getText().clear();

        }

    }

    public void confirmDelete(View view) {
        String password = passwordDeleteView.getText().toString();

        if (password.isEmpty()) {
            Toast.makeText(getBaseContext(), emptyPasswordToast, Toast.LENGTH_SHORT).show();
            checkOptions("Please confirm with old password!");
            return;

        }

        if (!password.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), wrongPasswordToast, Toast.LENGTH_SHORT).show();
            checkOptions("Actual password is incorrect!");
            return;

        }

        AuthCredential credential = EmailAuthProvider.getCredential(LoggedUserData.loggedUserEmail, LoggedUserData.loggedUserPassword);

        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                checkOptions("Account deleted successfully!");
                                FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).removeValue();
                                Toast.makeText(getBaseContext(), successDeleteToast, Toast.LENGTH_SHORT).show();
                                finishAndRemoveTask();
                            } else {
                                Toast.makeText(getBaseContext(), "Delete failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d("TAG", "Re-authenticate error!");

                }
            }

        });

    }

    private void destroySpeaker() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech = null;

        }

    }

    @Override
    protected void onDestroy() {
        destroySpeaker();
        super.onDestroy();

    }

    private void verifyTextToSpeechListenerStatus(int status) {
        if (status == TextToSpeech.SUCCESS) {
            setProgressListener();
            int result = textToSpeech.setLanguage(selectedLanguage);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(0.75f);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getBaseContext(), "Language not supported!", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(getBaseContext(), "Initialization failed!", Toast.LENGTH_SHORT).show();

        }

    }

    private void setTextToSpeechListener() {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            checkOptions("Welcome to EditData Activity!");
            setConnectionListener();

        });

    }

    private void setTextToSpeechListener(String feedback) {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            checkOptions(feedback);

        });

    }

    private void setProgressListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {


            }

            @Override
            public void onDone(String utteranceId) {
                speechRecognizer.destroy();
                Runnable runnable = () -> {
                    if (optionList.get(EXMIC).isValue()) {
                        getSpeechInput();

                    }
                };

                runOnUiThread(runnable);

            }

            @Override
            public void onError(String utteranceId) {

            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {

            }

        });

    }

    private void speak(String text, int queueMode) {
        textToSpeech.speak(text, queueMode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

    }

    private void speechInitialize() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);

    }

    private void getSpeechInput() {
        speechRecognizer.startListening(speechIntent);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                Log.d("Error", String.valueOf(error));
                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    getSpeechInput();

                }
                if (error == SpeechRecognizer.ERROR_NETWORK) {
                    speechRecognizer.destroy();
                    if (connectionStatus) {
                        connectionStatus = false;
                        Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
                        if (optionList.get(EXSPEAKER).isValue()) {
                            speak("Connection failed", QUEUE_ADD);

                        }

                    }

                }

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                voiceInput = result.get(0);
                Log.d("input", result.get(0));
                switch (LoggedUserData.language) {
                    case "english":
                    case "romanian":
                        speechInputEn(voiceInput);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

        });

    }

    private void invalidVoiceInput() {
        Toast.makeText(this, "Invalid command!", Toast.LENGTH_SHORT).show();
        checkOptions("Invalid command!");

    }

    private String usefulDataExtract(String voiceInput, int length) {
        String usefulData = voiceInput.substring(length);
        usefulData = usefulData.replaceAll("\\s", "");
        return usefulData;


    }

    private boolean checkNewUserNameCommand(String voiceInput) {
        short length = 0;
        boolean rule = false;
        if (voiceInput.startsWith("new user name ")) {
            rule = true;
            length = 14;

        }
        if (voiceInput.startsWith("new username ")) {
            rule = true;
            length = 13;

        }

        if (rule) {
            String userName = usefulDataExtract(voiceInput, length);
            newUserNameEditView.setText(userName);
            updateUserName(userName);
            return true;

        }

        return false;

    }

    private boolean checkSetOldPasswordCommand(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("old password ");
        if (rule) {
            String password = usefulDataExtract(voiceInput, 13);
            oldPasswordEditView.setText(password);
            checkOptions("Old password was set!");

        } else {
            return false;

        }
        return true;

    }

    private boolean checkNewPasswordCommand(String voiceInput) {
        boolean rule;
        String oldPassword = oldPasswordEditView.getText().toString();
        rule = voiceInput.startsWith("new password ");
        if (rule) {
            if (oldPassword.isEmpty()) {
                checkOptions("Old password not set!");
                return true;

            }
            String newPassword = usefulDataExtract(voiceInput, 13);
            newPasswordEditView.setText(newPassword);
            updatePassword(oldPassword,newPassword);
        } else {
            return false;

        }
        return true;

    }

    private boolean checkDeletePasswordCommand(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("delete password ");
        if (rule) {
            String password = usefulDataExtract(voiceInput, 16);
            passwordDeleteView.setText(password);
        } else {
            return false;

        }
        return true;

    }

    private void speechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkNewUserNameCommand(voiceInput)) {
            return;

        }
        if (checkSetOldPasswordCommand(voiceInput)) {
            return;

        }

        if (checkNewPasswordCommand(voiceInput)) {
            return;

        }

        if (checkDeletePasswordCommand(voiceInput)) {
            return;

        }

        switch (voiceInput) {
            case "english":
            case "English":
                if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.engLanguageRadioButton) {
                    engRadioButton.performClick();
                } else {
                    speak("English language is also selected!", QUEUE_ADD);

                }
                break;
            case "romanian":
            case "Romanian":
                if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.romLanguageRadioButton) {
                    romRadioButton.performClick();
                } else {
                    speak("Romanian language is also selected!", QUEUE_ADD);

                }
                break;
            case "bec":
                backButton.performClick();
                return;
            case "delete account":
                confirmDeleteButton.performClick();
                return;
            case "describe":
            case "described":
                checkOptions("Welcome to Edit Data Activity!");
                return;
            default:
                invalidVoiceInput();

        }

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && currentActivity instanceof EditDataActivity) {
                    Log.d("EditData","connectionListener");
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        connected();

                    } else {
                        lossConnection();

                    }
                }
                connectionListenerStatus = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Connection listener cancelled!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void connected() {
        connectionStatus = true;
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        speechRecognizer.destroy();
        checkOptions("Connected");

    }

    private void lossConnection() {
        if (!optionList.get(EXMIC).isValue()) {
            connectionStatus = false;
            Toast.makeText(getApplicationContext(), "Connection lost!", Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                speak("Connection lost!", QUEUE_ADD);

            }

        }

    }

}