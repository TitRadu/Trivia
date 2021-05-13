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
import android.widget.Switch;
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
import static com.example.triviaapp.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.LoggedUserData.connectionStatus;
import static com.example.triviaapp.LoggedUserData.currentActivity;
import static com.example.triviaapp.LoggedUserData.optionList;

public class EditDataActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    EditText newUserNameEditView, oldPasswordEditView, newPasswordEditView, passwordDeleteView;
    RadioGroup chooseLanguageRadioGroup;
    RadioButton engRadioButton, romRadioButton;
    Switch exMicSwitch, exSpeakerSwitch;
    Button editButton, backButton, deleteButton, confirmDeleteButton;
    TextView newUserNameTextView, oldPasswordTextView, newPasswordTextView, chooseLanguageTextView, exOptionsTextView;
    String existUserNameToastAudio, successUserNameToast, wrongPasswordToastAudio, shortPasswordToastAudio, successPasswordToastAudio, emptyPasswordToast, successDeleteToastAudio,
    userNameSetAudio, describeAudio, connectedToastAudio, connectionLostToastAudio, invalidCommandToastAudio, microphoneSelectAudio, microphoneDeselectAudio, speakerSelectAudio, speakerDeselectAudio,
    oldPasswordSetAudio;

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
        initializeViews();
        initialize();
        initializeRadioGroup();
        setExOptionSwitchListeners();
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
        exMicSwitch = findViewById(R.id.exMicOptionSwitch);
        exMicSwitch.setChecked(optionList.get(EXMIC).isValue());
        exSpeakerSwitch = findViewById(R.id.exSpeakerOptionSwitch);
        exSpeakerSwitch.setChecked(optionList.get(EXSPEAKER).isValue());
        confirmDeleteButton = findViewById(R.id.confirmDeleteButton);
        newUserNameTextView = findViewById(R.id.newUserNameTextView);
        oldPasswordTextView = findViewById(R.id.oldPasswordTextView);
        newPasswordTextView = findViewById(R.id.newPasswordTextView);
        chooseLanguageTextView = findViewById(R.id.chooseLanguageTextView);
        exOptionsTextView = findViewById(R.id.exOptionsTextView);
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
        exOptionsTextView.setText(R.string.exOptionsTextViewEditEn);
        exMicSwitch.setText(R.string.microphoneSwitchMenuEditPlayEn);
        exSpeakerSwitch.setText(R.string.loudSpeakerSwitchMenuEditEn);
        editButton.setText(R.string.editButtonEditEn);
        backButton.setText(R.string.backButtonEditEn);
        deleteButton.setText(R.string.deleteAccountButtonEditEn);
        passwordDeleteView.setHint(R.string.passwordHintLogRegEditEn);
        confirmDeleteButton.setText(R.string.confirmButtonEditEn);
        existUserNameToastAudio = getString(R.string.existUserNameToastAudioRegEditEn);
        successUserNameToast = getString(R.string.successUserNameToastEditEn);
        wrongPasswordToastAudio = getString(R.string.wrongPasswordToastAudioEditEn);
        shortPasswordToastAudio = getString(R.string.shortPasswordToastAudioRegEditEn);
        successPasswordToastAudio = getString(R.string.successPasswordToastAudioEditEn);
        emptyPasswordToast = getString(R.string.emptyPasswordToastEditEn);
        successDeleteToastAudio = getString(R.string.successDeleteToastAudioEditEn);
        userNameSetAudio = getString(R.string.userNameSetAudioEditEn);
        describeAudio = getString(R.string.describeAudioEditEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);
        oldPasswordSetAudio = getString(R.string.oldPasswordSetAudioEditEn);

    }


    private void setViewForRomanianLanguage() {
        newUserNameTextView.setText(R.string.userNameTextViewEditRou);
        oldPasswordTextView.setText(R.string.oldPasswordTextViewEditRou);
        newPasswordTextView.setText(R.string.newPasswordTextViewEditRou);
        chooseLanguageTextView.setText(R.string.chooseLanguageTextViewEditRou);
        exOptionsTextView.setText(R.string.exOptionsTextViewEditRou);
        exMicSwitch.setText(R.string.microphoneSwitchMenuEditPlayRou);
        exSpeakerSwitch.setText(R.string.loudSpeakerSwitchMenuEditRou);
        editButton.setText(R.string.editButtonEditRou);
        backButton.setText(R.string.backButtonEditRou);
        deleteButton.setText(R.string.deleteAccountButtonEditRou);
        passwordDeleteView.setHint(R.string.passwordHintLogRegEditRou);
        confirmDeleteButton.setText(R.string.confirmButtonEditRou);
        existUserNameToastAudio = getString(R.string.existUserNameToastAudioRegEditRou);
        successUserNameToast = getString(R.string.successUserNameToastEditRou);
        wrongPasswordToastAudio = getString(R.string.wrongPasswordToastAudioEditRou);
        shortPasswordToastAudio = getString(R.string.shortPasswordToastAudioRegEditRou);
        successPasswordToastAudio = getString(R.string.successPasswordToastAudioEditRou);
        emptyPasswordToast = getString(R.string.emptyPasswordToastEditRou);
        successDeleteToastAudio = getString(R.string.successDeleteToastAudioEditRou);
        userNameSetAudio = getString(R.string.userNameSetAudioEditRou);
        describeAudio = getString(R.string.describeAudioEditRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioRou);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioRou);
        microphoneSelectAudio = getString(R.string.microphoneSelectOptionAudioEditRou);
        microphoneDeselectAudio = getString(R.string.microphoneDeselectOptionAudioEditRou);
        speakerSelectAudio = getString(R.string.speakerSelectOptionAudioEditRou);
        speakerDeselectAudio = getString(R.string.speakerDeselectOptionAudioEditRou);
        oldPasswordSetAudio = getString(R.string.oldPasswordSetAudioEditRou);

    }

    private void chooseLanguage() {
        switch (LoggedUserData.language) {
            case "english":
                setViewForEnglishLanguage();
                selectedLanguage = Locale.ENGLISH;
                break;
            case "romanian":
                setViewForRomanianLanguage();
                selectedLanguage = Locale.getDefault();
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

    private void setExOptionSwitchListeners(){
        exMicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speechRecognizer.destroy();
            if (isChecked) {
                checkOptions(microphoneSelectAudio);

            } else {
                checkOptions(microphoneDeselectAudio);

            }
            SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(EXMIC).setValue(isChecked);
            editor.putString(optionList.get(EXMIC).getName(), String.valueOf(isChecked));
            editor.apply();

        });
        exSpeakerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speechRecognizer.destroy();
            if(!isChecked){
                checkOptions(speakerDeselectAudio);

            }
            SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(EXSPEAKER).setValue(isChecked);
            editor.putString(optionList.get(EXSPEAKER).getName(), String.valueOf(isChecked));
            editor.apply();
            if (isChecked) {
                checkOptions(speakerSelectAudio);

            }

        });

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
                    speechRecognizer.destroy();
                    setViewForEnglishLanguage();

                    selectedLanguage = Locale.ENGLISH;
                    speechInitialize();
                    setTextToSpeechListener(getString(R.string.englishLanguageSelectedAudioEditEn));

                    LoggedUserData.language = "english";
                    editor.putString("language", "english");
                    editor.apply();
                    break;
                case R.id.romLanguageRadioButton:
                    speechRecognizer.destroy();
                    setViewForRomanianLanguage();

                    selectedLanguage = Locale.getDefault();
                    speechInitialize();
                    setTextToSpeechListener(getString(R.string.romanianLanguageSelectedAudioEditRou));

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
            newUserNameEditView.getText().clear();
            Toast.makeText(getBaseContext(), existUserNameToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(existUserNameToastAudio);
            return;

        }
        LoggedUserData.loggedUserName = newUserName;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);
        Toast.makeText(getBaseContext(), successUserNameToast, Toast.LENGTH_SHORT).show();
        newUserNameEditView.getText().clear();
        checkOptions(userNameSetAudio + SPACESTRING + newUserName + "!");

    }

    private void updatePassword(String oldPassword, String newPassword) {
        if (oldPassword.isEmpty()) {
            return;

        }

        if (!oldPassword.equals(LoggedUserData.loggedUserPassword)) {
            clearPasswordInputs();
            Toast.makeText(getBaseContext(), wrongPasswordToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(wrongPasswordToastAudio);
            return;

        }

        if (newPassword.length() < 6) {
            newPasswordEditView.getText().clear();
            Toast.makeText(getBaseContext(), shortPasswordToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(shortPasswordToastAudio);
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
                                Toast.makeText(getBaseContext(), successPasswordToastAudio, Toast.LENGTH_SHORT).show();
                                clearPasswordInputs();
                                checkOptions(successPasswordToastAudio);
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
            checkOptions(emptyPasswordToast);
            return;

        }

        if (!password.equals(LoggedUserData.loggedUserPassword)) {
            Toast.makeText(getBaseContext(), wrongPasswordToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(wrongPasswordToastAudio);
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
                                checkOptions(successDeleteToastAudio);
                                FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).removeValue();
                                Toast.makeText(getBaseContext(), successDeleteToastAudio, Toast.LENGTH_SHORT).show();
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
            checkOptions(describeAudio);
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
                        Toast.makeText(getApplicationContext(), connectionLostToastAudio, Toast.LENGTH_SHORT).show();
                        if (optionList.get(EXSPEAKER).isValue()) {
                            speak(connectionLostToastAudio, QUEUE_ADD);

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
                        speechInputEn(voiceInput);
                        break;
                    case "romanian":
                        speechInputRou(voiceInput);
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
        Toast.makeText(this, invalidCommandToastAudio, Toast.LENGTH_SHORT).show();
        checkOptions(invalidCommandToastAudio);

    }

    private String usefulDataExtract(String voiceInput, int length) {
        String usefulData = voiceInput.substring(length);
        usefulData = usefulData.replaceAll("\\s", "");
        return usefulData;


    }

    private boolean checkNewUserNameCommandEn(String voiceInput) {
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

    private boolean checkNewUserNameCommandRou(String voiceInput) {
        if (voiceInput.startsWith("nume nou ")) {
            String userName = usefulDataExtract(voiceInput, 9);
            newUserNameEditView.setText(userName);
            updateUserName(userName);
            return true;

        }

        return false;

    }


    private boolean checkSetOldPasswordCommandEn(String voiceInput) {
        if (voiceInput.startsWith("old password ")) {
            String password = usefulDataExtract(voiceInput, 13);
            oldPasswordEditView.setText(password);
            checkOptions(oldPasswordSetAudio);

        } else {
            return false;

        }
        return true;

    }

    private boolean checkSetOldPasswordCommandRou(String voiceInput) {
        if (voiceInput.startsWith("parolă veche ")) {
            String password = usefulDataExtract(voiceInput, 13);
            oldPasswordEditView.setText(password);
            checkOptions(oldPasswordSetAudio);

        } else {
            return false;

        }
        return true;

    }

    private boolean checkNewPasswordCommandEn(String voiceInput) {
        String oldPassword = oldPasswordEditView.getText().toString();
        if (voiceInput.startsWith("new password ")) {
            if (oldPassword.isEmpty()) {
                checkOptions(emptyPasswordToast);
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

    private boolean checkNewPasswordCommandRou(String voiceInput) {
        String oldPassword = oldPasswordEditView.getText().toString();
        if (voiceInput.startsWith("parolă nouă ")) {
            if (oldPassword.isEmpty()) {
                checkOptions(emptyPasswordToast);
                return true;

            }
            String newPassword = usefulDataExtract(voiceInput, 12);
            newPasswordEditView.setText(newPassword);
            updatePassword(oldPassword,newPassword);
        } else {
            return false;

        }
        return true;

    }

    private boolean checkDeletePasswordCommandEn(String voiceInput) {
        if (voiceInput.startsWith("delete password ")) {
            String password = usefulDataExtract(voiceInput, 16);
            passwordDeleteView.setText(password);
            checkOptions(oldPasswordSetAudio);
        } else {
            return false;

        }
        return true;

    }

    private boolean checkDeletePasswordCommandRou(String voiceInput) {
        if (voiceInput.startsWith("parolă ștergere ")) {
            String password = usefulDataExtract(voiceInput, 16);
            passwordDeleteView.setText(password);
            checkOptions(oldPasswordSetAudio);
        } else {
            return false;

        }
        return true;

    }

    private void speechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkNewUserNameCommandEn(voiceInput)) {
            return;

        }
        if (checkSetOldPasswordCommandEn(voiceInput)) {
            return;

        }

        if (checkNewPasswordCommandEn(voiceInput)) {
            return;

        }

        if (checkDeletePasswordCommandEn(voiceInput)) {
            return;

        }

        switch (voiceInput) {
            case "english":
            case "English":
                if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.engLanguageRadioButton) {
                    engRadioButton.performClick();
                } else {
                    speak(getString(R.string.englishAlsoSelectedAudioEditEn), QUEUE_ADD);

                }
                break;
            case "romanian":
            case "Romanian":
                romRadioButton.performClick();
                break;
            case "microphone":
                exMicSwitch.performClick();
                break;
            case "speaker":
                exSpeakerSwitch.performClick();
                break;
            case "bec":
                backButton.performClick();
                return;
            case "delete account":
                confirmDeleteButton.performClick();
                return;
            case "describe":
            case "described":
                checkOptions(describeAudio);
                return;
            default:
                invalidVoiceInput();

        }

    }

    private void speechInputRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkNewUserNameCommandRou(voiceInput)) {
            return;

        }
        if (checkSetOldPasswordCommandRou(voiceInput)) {
            return;

        }

        if (checkNewPasswordCommandRou(voiceInput)) {
            return;

        }

        if (checkDeletePasswordCommandRou(voiceInput)) {
            return;

        }

        switch (voiceInput) {
            case "engleză":
            case "Engleză":
                engRadioButton.performClick();
                break;
            case "română":
            case "Română":
                if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.romLanguageRadioButton) {
                    romRadioButton.performClick();
                } else {
                    speak(getString(R.string.romanianAlsoSelectedAudioEditRou), QUEUE_ADD);

                }
                break;
            case "microfon":
                exMicSwitch.performClick();
                break;
            case "difuzor":
                exSpeakerSwitch.performClick();
                break;
            case "înapoi":
                backButton.performClick();
                return;
            case "șterge cont":
                confirmDeleteButton.performClick();
                return;
            case "descrie":
            case "descriere":
                checkOptions(describeAudio);
                return;
            default:
                invalidVoiceInput();

        }

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && currentActivity instanceof EditDataActivity && textToSpeech != null) {
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
        Toast.makeText(getApplicationContext(), connectedToastAudio, Toast.LENGTH_SHORT).show();
        speechRecognizer.destroy();
        checkOptions(connectedToastAudio);

    }

    private void lossConnection() {
        if (!optionList.get(EXMIC).isValue()) {
            connectionStatus = false;
            Toast.makeText(getApplicationContext(), connectionLostToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                speak(connectionLostToastAudio, QUEUE_ADD);

            }

        }

    }

}