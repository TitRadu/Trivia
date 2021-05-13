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

import com.example.triviaapp.game.GameActivity;
import com.example.triviaapp.rank.User;
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
import static com.example.triviaapp.LoggedUserData.EXMIC;
import static com.example.triviaapp.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.LoggedUserData.connectionStatus;
import static com.example.triviaapp.LoggedUserData.currentActivity;
import static com.example.triviaapp.LoggedUserData.optionList;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseHelper firebaseHelper;

    private EditText userNameInput, emailInput, passwordInput;

    Button createAccountButton;
    TextView alreadyHaveAccountTextView;
    String emptyEmailToast, emptyUserNameToast, emptyPasswordToast, shortPasswordToast, existUserNameToast, successCreateToast, existEmailToast;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        chooseLanguage();

    }

    private void initialize() {
        currentActivity = this;
        setTextToSpeechListener();
        prefs = getSharedPreferences("preferences.txt", MODE_PRIVATE);
        speechInitialize();

    }

    private void initializeViews() {
        userNameInput = findViewById(R.id.userNameRegInput);
        emailInput = findViewById(R.id.emailRegInput);
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
        createAccountButton.setText(R.string.createButtonLogRegEn);
        alreadyHaveAccountTextView.setText(R.string.alreadyHaveTextViewRegEn);
        emptyEmailToast = getString(R.string.emptyMailToastLogRegEn);
        emptyUserNameToast = getString(R.string.emptyUserNameToastRegEn);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEn);
        shortPasswordToast = getString(R.string.shortPasswordToastAudioRegEditEn);
        existUserNameToast = getString(R.string.existUserNameToastAudioRegEditEn);
        successCreateToast = getString(R.string.successCreateToastRegEn);
        existEmailToast = getString(R.string.existEmailToastRegEn);

    }

    private void setViewForRomanianLanguage() {
        userNameInput.setHint(R.string.userNameHintRegRou);
        passwordInput.setHint(R.string.passwordHintLogRegEditRou);
        createAccountButton.setText(R.string.createButtonLogRegRou);
        alreadyHaveAccountTextView.setText(R.string.alreadyHaveTextViewRegRou);
        emptyEmailToast = getString(R.string.emptyMailToastLogRegRou);
        emptyUserNameToast = getString(R.string.emptyUserNameToastRegRou);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegRou);
        shortPasswordToast = getString(R.string.shortPasswordToastAudioRegEditRou);
        existUserNameToast = getString(R.string.existUserNameToastAudioRegEditRou);
        successCreateToast = getString(R.string.successCreateToastRegRou);
        existEmailToast = getString(R.string.existEmailToastRegRou);

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

    private void updateMillis() {
        date = new Date();
        LoggedUserData.millis = date.getTime();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("millis", String.valueOf(LoggedUserData.millis));
        editor.apply();

    }

    private boolean inputCheck(String userName, String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, emptyEmailToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions("Introduce an email!");

            }
            return false;

        }

        if (userName.isEmpty()) {
            Toast.makeText(this, emptyUserNameToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions("Introduce an username!");

            }
            return false;

        }

        if (password.isEmpty()) {
            Toast.makeText(this, emptyPasswordToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions("Introduce a password!");

            }
            return false;

        }

        if (password.length() < 6) {
            Toast.makeText(this, shortPasswordToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                checkOptions("Password must contain minimum 6 characters!");

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

        if (!inputCheck(userName, email, password)) {
            return;

        }

        if (LoggedUserData.userNameList.contains(userName)) {
            Toast.makeText(getBaseContext(), existUserNameToast, Toast.LENGTH_SHORT).show();
            checkOptions("Username exists!");
            return;

        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        User registeredUser = new User(email, 0, password, 0, 0, 0, userName, 0, 0);
                        firebaseHelper.userDatabaseReference.child(UUID.randomUUID().toString()).setValue(registeredUser);
                        Toast.makeText(getBaseContext(), successCreateToast, Toast.LENGTH_SHORT).show();
                        LoggedUserData.loggedUserPassword = password;
                        LoggedUserData.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        updateMillis();
                        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                        startActivity(intent);
                        finishAndRemoveTask();

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getBaseContext(), existEmailToast, Toast.LENGTH_SHORT).show();
                        if (optionList.get(EXSPEAKER).isValue()) {
                            speak("Mail exists!", QUEUE_ADD);

                        } else {
                            if (optionList.get(EXMIC).isValue()) {
                                getSpeechInput();
                            }

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

    private void setTextToSpeechListener() {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            checkOptions("Welcome to Register Activity!");
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

    private boolean checkSetEmailCommand(String voiceInput) {
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
            checkOptions("Mail was set to " + email + "!");
            return true;

        }
        return false;

    }

    private boolean checkSetPasswordCommand(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("set password ");
        if (rule) {
            String password = usefulDataExtract(voiceInput, 13);
            passwordInput.setText(password);
            checkOptions("Password was set!");
            return true;
        }
        return false;

    }

    private boolean checkSetUserNameCommand(String voiceInput){
        short length = 0;
        boolean rule = false;
        if(voiceInput.startsWith("set user name ")){
            rule = true;
            length = 14;

        }
        if(voiceInput.startsWith("set username ")){
            rule = true;
            length = 13;

        }

        if(rule){
            String userName = usefulDataExtract(voiceInput, length);
            userNameInput.setText(userName);
            checkOptions("Username was set to " + userName + "!");
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

        if (checkSetEmailCommand(voiceInput)) {
            return;

        }
        if(checkSetUserNameCommand(voiceInput)){
            return;

        }

        if (checkSetPasswordCommand(voiceInput)) {
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
                    speak("Welcome to Register Activity!", QUEUE_ADD);

                }
                return;
            default:
                invalidVoiceInput();

        }

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(connectionListenerStatus && currentActivity instanceof RegisterActivity) {
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

    private void connected(){
        connectionStatus = true;
        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
        speechRecognizer.destroy();
        checkOptions("Connected");

    }

    private void lossConnection(){
        if(!optionList.get(EXMIC).isValue()) {
            connectionStatus = false;
            Toast.makeText(getApplicationContext(),"Connection lost!",Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                speak("Connection lost!",QUEUE_ADD);

            }

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