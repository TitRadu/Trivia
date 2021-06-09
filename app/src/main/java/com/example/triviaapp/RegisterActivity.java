package com.example.triviaapp;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.data.LoggedUserData;
import com.example.triviaapp.game.GameActivity;
import com.example.triviaapp.data.rank.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.data.LoggedUserData.EXMIC;
import static com.example.triviaapp.data.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.data.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.data.LoggedUserData.optionList;
import static com.example.triviaapp.data.LoggedUserData.voiceCommandLoginData;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseHelper firebaseHelper;

    private EditText userNameInput, emailInput, codeInput, passwordInput;

    Button createAccountButton;
    TextView alreadyHaveAccountTextView;
    String emptyEmailToastAudio, emptyUserNameToastAudio, emptyPasswordToastAudio, shortPasswordToastAudio, existUserNameToastAudio,
            successCreateToast, existEmailToastAudio, describeAudio, setMailAudio, setPasswordAudio, userNameSetAudio, describeCommandsAudio,
            connectedToastAudio, lostConnectionToastAudio, invalidCommandToastAudio, emptyCodeToastAudio, invalidCodeFormatToastAudio, shortCodeToastAudio,
            codeSetAudio;

    Date date;
    SharedPreferences prefs;

    private TextToSpeech textToSpeech;
    Locale selectedLanguage;

    String voiceInput = null;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;

    private boolean connectionListenerStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialize();
        initializeViews();
        setExtendedOptions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        chooseLanguage();

    }

    private void initialize() {
        prefs = getSharedPreferences("preferences.txt", MODE_PRIVATE);
        speechInitialize();

    }

    private void initializeViews() {
        userNameInput = findViewById(R.id.userNameRegInput);
        emailInput = findViewById(R.id.emailRegInput);
        codeInput = findViewById(R.id.codeRegInput);
        passwordInput = findViewById(R.id.passwordRegInput);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseHelper = FirebaseHelper.getInstance();
        createAccountButton = findViewById(R.id.createAccountButton);
        alreadyHaveAccountTextView = findViewById(R.id.im_already_have_an_account);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage() {
        userNameInput.setHint(R.string.userNameHintRegEn);
        passwordInput.setHint(R.string.passwordHintLogRegEditEn);
        codeInput.setHint(R.string.codeHintRegEn);
        createAccountButton.setText(R.string.createButtonLogRegEn);
        alreadyHaveAccountTextView.setText(R.string.alreadyHaveTextViewRegEn);
        emptyEmailToastAudio = getString(R.string.emptyMailToastAudioLogRegEn);
        emptyUserNameToastAudio = getString(R.string.emptyUserNameToastRegEn);
        emptyPasswordToastAudio = getString(R.string.emptyPasswordToastAudioLogRegEn);
        shortPasswordToastAudio = getString(R.string.shortPasswordToastAudioRegEditEn);
        existUserNameToastAudio = getString(R.string.existUserNameToastAudioRegEditEn);
        successCreateToast = getString(R.string.successCreateToastRegEn);
        existEmailToastAudio = getString(R.string.existEmailToastRegEn);
        describeAudio = getString(R.string.describeAudioRegEn);
        setMailAudio = getString(R.string.mailSetAudioLogRegEn);
        setPasswordAudio = getString(R.string.passwordSetAudioLogRegEn);
        userNameSetAudio = getString(R.string.userNameSetAudioRegEditEn);
        describeCommandsAudio = getString(R.string.describeCommandsAudioLogEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        lostConnectionToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);
        emptyCodeToastAudio = getString(R.string.emptyCodeToastAudioLogEn);
        invalidCodeFormatToastAudio = getString(R.string.invalidCodeFormatToastAudioLogEn);
        shortCodeToastAudio = getString(R.string.shortCodeToastAudioLogEn);

    }

    private void setViewForRomanianLanguage() {
        userNameInput.setHint(R.string.userNameHintRegRou);
        passwordInput.setHint(R.string.passwordHintLogRegEditRou);
        codeInput.setHint(R.string.codeHintRegRou);
        createAccountButton.setText(R.string.createButtonLogRegRou);
        alreadyHaveAccountTextView.setText(R.string.alreadyHaveTextViewRegRou);
        emptyEmailToastAudio = getString(R.string.emptyMailToastAudioLogRegRou);
        emptyUserNameToastAudio = getString(R.string.emptyUserNameToastRegRou);
        emptyPasswordToastAudio = getString(R.string.emptyPasswordToastAudioLogRegRou);
        shortPasswordToastAudio = getString(R.string.shortPasswordToastAudioRegEditRou);
        existUserNameToastAudio = getString(R.string.existUserNameToastAudioRegEditRou);
        successCreateToast = getString(R.string.successCreateToastRegRou);
        existEmailToastAudio = getString(R.string.existEmailToastRegRou);
        describeAudio = getString(R.string.describeAudioRegRou);
        setMailAudio = getString(R.string.mailSetAudioLogRegRou);
        setPasswordAudio = getString(R.string.passwordSetAudioLogRegRou);
        userNameSetAudio = getString(R.string.userNameSetAudioRegEditRou);
        describeCommandsAudio = getString(R.string.describeCommandsAudioLogRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        lostConnectionToastAudio = getString(R.string.connectionLostToastAudioRou);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioRou);
        emptyCodeToastAudio = getString(R.string.emptyCodeToastAudioLogRou);
        invalidCodeFormatToastAudio = getString(R.string.invalidCodeFormatToastAudioLogRou);
        shortCodeToastAudio = getString(R.string.shortCodeToastAudioLogRou);

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

    private void updateMillis() {
        date = new Date();
        LoggedUserData.millis = date.getTime();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("millis", String.valueOf(LoggedUserData.millis));
        editor.apply();

    }

    private boolean inputCheck(String userName, String email,String code, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, emptyEmailToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(emptyEmailToastAudio);

            }
            return false;

        }

        if (userName.isEmpty()) {
            Toast.makeText(this, emptyUserNameToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(emptyUserNameToastAudio);

            }
            return false;

        }

        if (code.isEmpty()) {
            Toast.makeText(this,emptyCodeToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(emptyCodeToastAudio);

            }
            return false;

        }

        if (!code.matches("\\d+")) {
            Toast.makeText(this, invalidCodeFormatToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(invalidCodeFormatToastAudio);

            }
            return false;

        }

        if (code.length() < 6) {
            Toast.makeText(this, shortCodeToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(shortCodeToastAudio);

            }
            return false;

        }

        if (password.isEmpty()) {
            Toast.makeText(this, emptyPasswordToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(emptyPasswordToastAudio);

            }
            return false;

        }

        if (password.length() < 6) {
            Toast.makeText(this, shortPasswordToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions(shortPasswordToastAudio);

            }
            return false;

        }

        return true;
    }

    public void createAccount(View view) {
        speechRecognizer.destroy();
        final String userName = userNameInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String loginCode = codeInput.getText().toString();

        if (!inputCheck(userName, email, loginCode, password)) {
            return;

        }

        if (LoggedUserData.userNameList.contains(userName)) {
            Toast.makeText(getBaseContext(), existUserNameToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(existUserNameToastAudio);
            return;

        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        User registeredUser = new User(email, 0, password, 0, 0, 0, userName, loginCode, 0, 0);
                        firebaseHelper.userDatabaseReference.child(UUID.randomUUID().toString()).setValue(registeredUser);
                        Toast.makeText(getBaseContext(), successCreateToast, Toast.LENGTH_SHORT).show();
                        LoggedUserData.loggedUserPassword = password;
                        LoggedUserData.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        updateMillis();

                        String data = prefs.getString(email, "Key not found!");

                        if (data.equals("Key not found!")) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(email, loginCode);
                            editor.apply();

                        }

                        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                        startActivity(intent);
                        finishAndRemoveTask();

                    } else {
                        if(voiceCommandLoginData.containsKey(email)){
                            Toast.makeText(getBaseContext(), existEmailToastAudio, Toast.LENGTH_SHORT).show();
                            checkOptions(existEmailToastAudio);
                            return;

                        }

                    }

                });

    }

    public void hasAnAccount(View view) {
        speechRecognizer.destroy();
        LoggedUserData.onResumeFromAnotherActivity = true;
        finishAndRemoveTask();

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

    private void setExtendedOptions() {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            checkOptions(describeAudio);
            setConnectionListener();

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
        if (textToSpeech != null) {
            textToSpeech.speak(text, queueMode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        } else {
            if (optionList.get(EXMIC).isValue()) {
                getSpeechInput();

            }

        }

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

    String getPlatformFromVoiceInput(String email) {
        String platform = null;
        if (email.endsWith("yahoo")) {
            platform = "yahoo";

        }
        if (email.endsWith("gmail")) {
            platform = "gmail";

        }

        return platform;

    }

    private boolean checkSetEmailCommandEn(String voiceInput) {
        String platform;
        boolean rule;

        rule = voiceInput.startsWith("set email ");
        if (rule) {
            String email = usefulDataExtract(voiceInput, 9);
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput();
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            emailInput.setText(email);
            checkOptions(setMailAudio + SPACESTRING + email + "!");
            return true;

        }
        return false;

    }

    private boolean checkSetEmailCommandRou(String voiceInput) {
        String platform;
        boolean rule;

        rule = voiceInput.startsWith("setează email ");
        if (rule) {
            String email = usefulDataExtract(voiceInput, 14);
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput();
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            emailInput.setText(email);
            checkOptions(setMailAudio + SPACESTRING + email + "!");
            return true;

        }
        return false;

    }

    private boolean checkSetPasswordCommandEn(String voiceInput) {
        if (voiceInput.startsWith("set password ")) {
            String password = usefulDataExtract(voiceInput, 13);
            passwordInput.setText(password);
            checkOptions(setPasswordAudio);
            return true;
        }
        return false;

    }

    private boolean checkSetPasswordCommandRou(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("setează parolă ");
        if (rule) {
            String password = usefulDataExtract(voiceInput, 15);
            passwordInput.setText(password);
            checkOptions(setPasswordAudio);
            return true;
        }
        return false;

    }

    private boolean checkSetUserNameCommandEn(String voiceInput) {
        short length = 0;
        boolean rule = false;
        if (voiceInput.startsWith("set user name ")) {
            rule = true;
            length = 14;

        }
        if (voiceInput.startsWith("set username ")) {
            rule = true;
            length = 13;

        }

        if (rule) {
            String userName = usefulDataExtract(voiceInput, length);
            userNameInput.setText(userName);
            checkOptions(userNameSetAudio + SPACESTRING + userName + "!");
            return true;

        }
        return false;

    }

    private boolean checkSetUserNameCommandRou(String voiceInput) {
        if (voiceInput.startsWith("setează nume ")) {
            String userName = usefulDataExtract(voiceInput, 13);
            userNameInput.setText(userName);
            checkOptions(userNameSetAudio + SPACESTRING + userName + "!");
            return true;

        }
        return false;

    }

    private boolean checkSetCodeCommandEn(String voiceInput) {
        if (voiceInput.startsWith("set code ")) {
            String code = usefulDataExtract(voiceInput, 9);
            codeInput.setText(code);
            checkOptions(userNameSetAudio + SPACESTRING + code + "!");
            return true;

        }
        return false;

    }

    private boolean checkSetCodeCommandRou(String voiceInput) {
        if (voiceInput.startsWith("setează cod ")) {
            String code = usefulDataExtract(voiceInput, 12);
            codeInput.setText(code);
            checkOptions(userNameSetAudio + SPACESTRING + code + "!");
            return true;

        }
        return false;

    }

    private String usefulDataExtract(String voiceInput, int length) {
        String usefulData = voiceInput.substring(length);
        usefulData = usefulData.replaceAll("\\s", "");
        return usefulData;


    }

    private void speechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkSetEmailCommandEn(voiceInput)) {
            return;

        }
        if (checkSetUserNameCommandEn(voiceInput)) {
            return;

        }
        if (checkSetCodeCommandEn(voiceInput)) {
            return;

        }
        if (checkSetPasswordCommandEn(voiceInput)) {
            return;

        }
        switch (voiceInput) {
            case "create account":
                createAccountButton.performClick();
                return;
            case "back":
                alreadyHaveAccountTextView.performClick();
                return;
            case "describe":
            case "described":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput();

                }
                return;
            case "commands":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeCommandsAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput();

                }
                break;
            default:
                invalidVoiceInput();

        }

    }

    private void speechInputRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkSetEmailCommandRou(voiceInput)) {
            return;

        }
        if (checkSetUserNameCommandRou(voiceInput)) {
            return;

        }
        if (checkSetCodeCommandRou(voiceInput)) {
            return;

        }
        if (checkSetPasswordCommandRou(voiceInput)) {
            return;

        }
        switch (voiceInput) {
            case "creează cont":
                createAccountButton.performClick();
                return;
            case "înapoi":
                alreadyHaveAccountTextView.performClick();
                return;
            case "descrie":
            case "descriere":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput();

                }
                return;
            case "comenzi":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeCommandsAudio, QUEUE_ADD);

                } else {
                    invalidVoiceInput();

                }
                break;
            default:
                invalidVoiceInput();

        }

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && textToSpeech != null) {
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
        Toast.makeText(getApplicationContext(), connectedToastAudio, Toast.LENGTH_SHORT).show();
        speechRecognizer.destroy();
        checkOptions(connectedToastAudio);

    }

    private void lossConnection() {
        Toast.makeText(getApplicationContext(), lostConnectionToastAudio, Toast.LENGTH_SHORT).show();
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(lostConnectionToastAudio, QUEUE_ADD);
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

}