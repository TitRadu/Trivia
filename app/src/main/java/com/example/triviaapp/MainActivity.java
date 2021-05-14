package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.LoggedUserData.EXMIC;
import static com.example.triviaapp.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.LoggedUserData.connectionStatus;
import static com.example.triviaapp.LoggedUserData.currentActivity;
import static com.example.triviaapp.LoggedUserData.language;
import static com.example.triviaapp.LoggedUserData.onResumeFromAnotherActivity;
import static com.example.triviaapp.LoggedUserData.optionList;

public class MainActivity extends AppCompatActivity {
    public static final Integer RECORD_AUDIO = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedUser;

    private EditText emailInput, passwordInput, forgotPasswordEmailInput;

    private LinearLayout forgotPasswordLayout;

    Button logInButton, createAccountButton, sendMailButton;
    TextView welcomePopUpTextView, chooseLanguagePopUpTextView, chooseInteractionPopUpTextView, forgotPasswordTextView;
    String emptyMailToastAudio, emptyPasswordToastAudio, successDataToast, wrongDataToastAudio, successSendMailToastAudio, wrongMailToastAudio, audioGrantedToastAudio, audioDeniedToastAudio,
    describePopUpAudio, describeAudio, describeAudioPermissionAudio, describeCommandsAudio,restartPresentationAudio, microphoneSelectAudio, microphoneDeselectAudio, speakerSelectAudio, speakerDeselectAudio,
    selectALanguageToastAudio, mailSetAudio, passwordSetAudio, connectedToastAudio, connectionLostToastAudio, invalidCommandToastAudio;

    Date date;
    SharedPreferences prefs;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private RadioGroup chooseLanguageRadioGroup;
    private RadioButton engRadioButton, romRadioButton;
    Switch extendedSwitchMicrophone, extendedSwitchSpeaker;
    private Button continueButtonPopUp;
    private Button muteButtonPopUp;

    private TextToSpeech textToSpeech;
    Locale selectedLanguage;

    String voiceInput = null;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    List<String> emailList;
    String currentScreen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        initializeMicrophoneStatusAndCategoriesOptions();
        initializeViews();
        initializeUserNameList();
        setTextToSpeechListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
        if (onResumeFromAnotherActivity) {
            setTextToSpeechListener(describeAudio);
            //speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            chooseLanguage();

        }

        onResumeFromAnotherActivity = false;
    }

    private void initialize() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        selectedLanguage = Locale.ENGLISH;
        prefs = getSharedPreferences("preferences.txt", MODE_PRIVATE);
        speechInitialize();

    }

    private void initializeViews() {
        FirebaseHelper.getInstance();
        LoggedUserData.userNameList = new ArrayList<>();
        emailList = new ArrayList<>();
        emailInput = findViewById(R.id.emailLogInput);
        passwordInput = findViewById(R.id.passwordLogInput);
        forgotPasswordEmailInput = findViewById(R.id.forgotPasswordEmailInput);
        forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout);
        firebaseAuth = FirebaseAuth.getInstance();

        logInButton = findViewById(R.id.logInButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        sendMailButton = findViewById(R.id.sendMailButton);
        forgotPasswordTextView = findViewById(R.id.tv_forgotPassword);

        chooseLanguage();

    }

    private void setViewForEnglishLanguage() {
        describeCommandsAudio = getString(R.string.describeCommandsAudioLogEn);
        describeAudioPermissionAudio = getString(R.string.describeAudioPermissionAudioLogEn);
        describePopUpAudio = getString(R.string.describePopUpAudioLogEn);
        describeAudio = getString(R.string.describeAudioLogEn);
        passwordInput.setHint(R.string.passwordHintLogRegEditEn);
        logInButton.setText(R.string.logInButtonLogRegEn);
        createAccountButton.setText(R.string.createButtonLogRegEn);
        forgotPasswordTextView.setText(R.string.forgotPasswordTextViewLogEn);
        sendMailButton.setText(R.string.sendMailButtonLogEn);
        emptyMailToastAudio = getString(R.string.emptyMailToastAudioLogRegEn);
        emptyPasswordToastAudio = getString(R.string.emptyPasswordToastAudioLogRegEn);
        successDataToast = getString(R.string.successDataToastLogEn);
        wrongDataToastAudio = getString(R.string.wrongDataToastAudioLogEn);
        successSendMailToastAudio = getString(R.string.successSendMailToastAudioLogEn);
        wrongMailToastAudio = getString(R.string.wrongMailToastAudioLogEn);
        audioGrantedToastAudio = getString(R.string.audioGrantedToastAudioLogEn);
        audioDeniedToastAudio = getString(R.string.audioDeniedToastAudioLogEn);
        mailSetAudio = getString(R.string.mailSetAudioLogEn);
        passwordSetAudio = getString(R.string.passwordSetAudioLogEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);

    }

    private void setViewForRomanianLanguage() {
        describeCommandsAudio = getString(R.string.describeCommandsAudioLogRou);
        describeAudioPermissionAudio = getString(R.string.describeAudioPermissionAudioLogRou);
        describePopUpAudio = getString(R.string.describePopUpAudioLogRou);
        describeAudio = getString(R.string.describeAudioLogRou);
        passwordInput.setHint(R.string.passwordHintLogRegEditRou);
        logInButton.setText(R.string.logInButtonLogRegRou);
        createAccountButton.setText(R.string.createButtonLogRegRou);
        forgotPasswordTextView.setText(R.string.forgotPasswordTextViewLogRou);
        sendMailButton.setText(R.string.sendMailButtonLogRou);
        emptyMailToastAudio = getString(R.string.emptyMailToastAudioLogRegRou);
        emptyPasswordToastAudio = getString(R.string.emptyPasswordToastAudioLogRegRou);
        successDataToast = getString(R.string.successDataToastLogRou);
        wrongDataToastAudio = getString(R.string.wrongDataToastAudioLogRou);
        successSendMailToastAudio = getString(R.string.successSendMailToastAudioLogRou);
        wrongMailToastAudio = getString(R.string.wrongMailToastAudioLogRou);
        audioGrantedToastAudio = getString(R.string.audioGrantedToastAudioLogRou);
        audioDeniedToastAudio = getString(R.string.audioDeniedToastAudioLogRou);
        mailSetAudio = getString(R.string.mailSetAudioLogRou);
        passwordSetAudio = getString(R.string.passwordSetAudioLogRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioRou);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioRou);

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

    private boolean initializeLoggedUser() {
        loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        if (loggedUser != null) {
            String data = prefs.getString("millis", "Key not found!");
            LoggedUserData.millis = Long.parseLong(data);
            updateUI();
            return true;

        }
        return false;

    }

    private void setActivityStartPopUp() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            currentScreen = "Nothing";
            speak(describeAudioPermissionAudio, QUEUE_ADD);
            checkPermission();

        } else {
            if (!checkIfCompletedOptionsPopUp()) {
                currentScreen = "PopUp";
                speak(describePopUpAudio, QUEUE_ADD);
                chooseOptionsPopUp();
            } else {
                currentScreen = "Activity";
                if (!initializeLoggedUser()) {
                    checkOptions(describeAudio, "Activity");

                }

            }

        }

    }

    private boolean inputCheck(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, emptyMailToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(emptyMailToastAudio, "Activity");
            return false;

        }

        if (password.isEmpty()) {
            Toast.makeText(this, emptyPasswordToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(emptyPasswordToastAudio, "Activity");
            return false;

        }

        return true;

    }

    private void clearInputs() {
        emailInput.getText().clear();
        passwordInput.getText().clear();
        forgotPasswordEmailInput.getText().clear();
        forgotPasswordLayout.setVisibility(View.GONE);

    }

    public void registerActivity(View view) {
        speechRecognizer.destroy();
        destroySpeaker();
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        clearInputs();

    }

    private void updateMillis() {
        date = new Date();
        LoggedUserData.millis = date.getTime();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("millis", String.valueOf(LoggedUserData.millis));
        editor.apply();

    }

    public void logInActivity(View view) {
        speechRecognizer.destroy();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!inputCheck(email, password)) {
            return;

        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            speechRecognizer.destroy();
                            LoggedUserData.loggedUserPassword = password;
                            Toast.makeText(getBaseContext(), successDataToast, Toast.LENGTH_SHORT).show();
                            updateMillis();
                            updateUI();
                            clearInputs();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getBaseContext(), wrongDataToastAudio, Toast.LENGTH_SHORT).show();
                            checkOptions(wrongDataToastAudio, "Activity");

                        }

                    }

                });

    }

    private void updateUI() {
        destroySpeaker();
        currentActivity = new GameActivity();
        LoggedUserData.loggedUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);

    }

    private void initializeUserNameList() {
        FirebaseHelper.userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and NO again
                // whenever data at this location is updated.
                LoggedUserData.userNameList = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user = dataSnapshot1.getValue(User.class);
                    LoggedUserData.userNameList.add(user.getUserName());
                    emailList.add(user.getEmail());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), "User name not found!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    public void changeForgotLayoutVisibility(View view) {
        if (forgotPasswordLayout.getVisibility() == View.GONE) {
            forgotPasswordLayout.setVisibility(View.VISIBLE);
        } else {
            forgotPasswordLayout.setVisibility(View.GONE);
            forgotPasswordEmailInput.getText().clear();

        }

    }

    public void sendEmail(View view) {
        speechRecognizer.destroy();
        String email = forgotPasswordEmailInput.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, emptyMailToastAudio, Toast.LENGTH_SHORT).show();
            checkOptions(emptyMailToastAudio, "Activity");
            return;

        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), successSendMailToastAudio, Toast.LENGTH_SHORT).show();
                    checkOptions(successSendMailToastAudio, "Activity");

                } else {
                    Toast.makeText(getApplicationContext(), wrongMailToastAudio, Toast.LENGTH_SHORT).show();
                    checkOptions(wrongMailToastAudio, "Activity");

                }

            }

        });

    }

    private void checkPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, audioGrantedToastAudio, Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, audioDeniedToastAudio, Toast.LENGTH_SHORT).show();

            }
            if (!checkIfCompletedOptionsPopUp()) {
                currentScreen = "PopUp";
                speak(describePopUpAudio, QUEUE_ADD);
                chooseOptionsPopUp();
            } else {
                currentScreen = "Activity";
                checkOptions(describeAudio, "Activity");

            }

        }

    }

    private void initializeOptionList() {
        optionList = new ArrayList<>();
        optionList.add(new Option("mic", true));
        optionList.add(new Option("speaker", true));
        optionList.add(new Option("sport", true));
        optionList.add(new Option("geography", true));
        optionList.add(new Option("maths", true));
        optionList.add(new Option("others", true));
        optionList.add(new Option("exMic", true));
        optionList.add(new Option("exSpeaker", true));

    }

    private void initializeMicrophoneStatusAndCategoriesOptions() {
        initializeOptionList();
        String data;

        for (Option option : optionList) {
            data = prefs.getString(option.getName(), "Key not found!");

            if (data.equals("Key not found!")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(option.getName(), "true");
                editor.apply();
                option.setValue(true);

            } else {
                option.setValue(data.equals("true"));

            }

        }

        data = prefs.getString("language", "Key not found!");
        if (data.equals("Key not found!")) {
            SharedPreferences.Editor editor = prefs.edit();
            language = "english";
            editor.putString("language", "english");
            editor.apply();

        } else {
            language = data;

        }

        data = prefs.getString("millis", "Key not found!");
        if (data.equals("Key not found!")) {
            updateMillis();

        }

    }

    private void languageChooseListener() {
        SharedPreferences.Editor editor = prefs.edit();
        chooseLanguageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.engLanguageRadioButton:
                    speechRecognizer.destroy();
                    setViewForEnglishLanguage();
                    setPopUpViewsForEnglishLanguage();

                    selectedLanguage = Locale.ENGLISH;
                    speechInitialize();
                    setTextToSpeechListener(getString(R.string.englishLanguageSelectedAudioLogEditEn));


                    LoggedUserData.language = "english";
                    editor.putString("language", "english");
                    editor.apply();
                    break;
                case R.id.romLanguageRadioButton:
                    speechRecognizer.destroy();
                    setViewForRomanianLanguage();
                    setPopUpViewsForRomanianLanguage();

                    selectedLanguage = Locale.getDefault();
                    speechInitialize();
                    setTextToSpeechListener(getString(R.string.romanianLanguageSelectedAudioLogEditRou));

                    LoggedUserData.language = "romanian";
                    editor.putString("language", "romanian");
                    editor.apply();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + checkedId);
            }

        });

    }

    private void verifyContinueButtonAction() {
        speechRecognizer.destroy();

        if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.engLanguageRadioButton && chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.romLanguageRadioButton) {
            Toast.makeText(this, selectALanguageToastAudio, Toast.LENGTH_SHORT).show();
            speak(selectALanguageToastAudio, QUEUE_ADD);
            return;

        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("startApp", "true");
        editor.apply();
        dialog.dismiss();
        currentScreen = "Activity";
        checkOptions(describeAudio, "Activity");

    }

    private void interactionChooseListener() {
        extendedSwitchMicrophone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speechRecognizer.destroy();
            if (isChecked) {
                speak(microphoneSelectAudio, QUEUE_ADD);

            } else {
                speak(microphoneDeselectAudio, QUEUE_ADD);

            }
            SharedPreferences.Editor editor = prefs.edit();
            optionList.get(EXMIC).setValue(isChecked);
            editor.putString("exMic", String.valueOf(isChecked));
            editor.apply();

        });
        extendedSwitchSpeaker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speechRecognizer.destroy();
            if (isChecked) {
                speak(speakerSelectAudio, QUEUE_ADD);

            } else {
                speak(speakerDeselectAudio, QUEUE_ADD);

            }
            SharedPreferences.Editor editor = prefs.edit();
            optionList.get(EXSPEAKER).setValue(isChecked);
            editor.putString("exSpeaker", String.valueOf(isChecked));
            editor.apply();

        });

    }

    private void setOptionsPopUpViewsEnableState(boolean state) {
        extendedSwitchMicrophone.setEnabled(state);
        extendedSwitchSpeaker.setEnabled(state);
        engRadioButton.setEnabled(state);
        romRadioButton.setEnabled(state);
        continueButtonPopUp.setEnabled(state);

    }

    private void chooseOptionsPopUp() {
        dialogBuilder = new AlertDialog.Builder(this);
        View questionPopUpView = getLayoutInflater().inflate(R.layout.start_app_pop_up, null);
        welcomePopUpTextView = questionPopUpView.findViewById(R.id.welcomeTextView);
        chooseLanguagePopUpTextView = questionPopUpView.findViewById(R.id.chooseLanguageTextView);
        chooseInteractionPopUpTextView = questionPopUpView.findViewById(R.id.chooseInteractionTextView);
        chooseLanguageRadioGroup = questionPopUpView.findViewById(R.id.chooseLanguageRadioGroup);
        engRadioButton = questionPopUpView.findViewById(R.id.engLanguageRadioButton);
        romRadioButton = questionPopUpView.findViewById(R.id.romLanguageRadioButton);
        extendedSwitchMicrophone = questionPopUpView.findViewById(R.id.extendedMicrophoneSwitch);
        extendedSwitchMicrophone.setChecked(true);
        extendedSwitchSpeaker = questionPopUpView.findViewById(R.id.extendedSpeakerSwitch);
        extendedSwitchSpeaker.setChecked(true);
        continueButtonPopUp = questionPopUpView.findViewById(R.id.continueButtonPopUp);
        muteButtonPopUp = questionPopUpView.findViewById(R.id.muteButtonPopUp);

        setOptionsPopUpViewsEnableState(false);

        dialogBuilder.setView(questionPopUpView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        languageChooseListener();
        interactionChooseListener();
        continueButtonPopUp.setOnClickListener((v) -> verifyContinueButtonAction());
        muteButtonPopUp.setOnClickListener((v) -> {
            speechRecognizer.destroy();
            textToSpeech.stop();
            speak(restartPresentationAudio, QUEUE_ADD);
        });

    }

    private void setPopUpViewsForEnglishLanguage(){
        welcomePopUpTextView.setText(R.string.welcomeTextViewLogEn);
        chooseLanguagePopUpTextView.setText(R.string.chooseLanguageTextViewLogEditEn);
        chooseInteractionPopUpTextView.setText(R.string.exOptionsTextViewLogEditEn);
        extendedSwitchMicrophone.setText(R.string.microphoneSwitchLogMenuEditPlayEn);
        extendedSwitchSpeaker.setText(R.string.loudSpeakerSwitchLogMenuEditEn);
        muteButtonPopUp.setText(R.string.muteButtonLogEn);
        continueButtonPopUp.setText(R.string.nextButtonLogMenuPlayEn);
        restartPresentationAudio = getString(R.string.restartPresentationAudioLogEn);
        microphoneSelectAudio = getString(R.string.microphoneSelectOptionAudioLogEditEn);
        microphoneDeselectAudio = getString(R.string.microphoneDeselectOptionAudioLogEditEn);
        speakerSelectAudio = getString(R.string.speakerSelectOptionAudioLogEditEn);
        speakerDeselectAudio = getString(R.string.speakerDeselectOptionAudioLogEditEn);
        selectALanguageToastAudio = getString(R.string.selectALanguageToastAudioLogEn);

    }

    private void setPopUpViewsForRomanianLanguage(){
        welcomePopUpTextView.setText(R.string.welcomeTextViewLogRou);
        chooseLanguagePopUpTextView.setText(R.string.chooseLanguageTextViewLogEditRou);
        chooseInteractionPopUpTextView.setText(R.string.exOptionsTextViewLogEditRou);
        extendedSwitchMicrophone.setText(R.string.microphoneSwitchLogMenuEditPlayRou);
        extendedSwitchSpeaker.setText(R.string.loudSpeakerSwitchLogMenuEditRou);
        muteButtonPopUp.setText(R.string.muteButtonLogRou);
        continueButtonPopUp.setText(R.string.nextButtonLogMenuPlayRou);
        restartPresentationAudio = getString(R.string.restartPresentationAudioLogRou);
        microphoneSelectAudio = getString(R.string.microphoneSelectOptionAudioLogEditRou);
        microphoneDeselectAudio = getString(R.string.microphoneDeselectOptionAudioLogEditRou);
        speakerSelectAudio = getString(R.string.speakerSelectOptionAudioLogEditRou);
        speakerDeselectAudio = getString(R.string.speakerDeselectOptionAudioLogEditRou);
        selectALanguageToastAudio = getString(R.string.selectALanguageToastAudioLogRou);

    }

    private boolean checkIfCompletedOptionsPopUp() {
        String data = prefs.getString("startApp", "Key not found!");
        return data.equals("true");

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
            setActivityStartPopUp();
            setConnectionListener();

        });

    }

    private void setTextToSpeechListener(String feedback) {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            switch(currentScreen){
                case "Activity":
                    checkOptions(feedback, "Activity");
                    break;
                case "PopUp":
                    speak(feedback, QUEUE_ADD);
                    break;

            }

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
                    if (currentScreen.equals("PopUp")) {
                        setOptionsPopUpViewsEnableState(true);
                        getSpeechInput("PopUp");

                    }
                    if (currentScreen.equals("Activity")) {
                        if (optionList.get(EXMIC).isValue()) {
                            getSpeechInput("Activity");

                        }

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
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);

    }

    private void getSpeechInput(String control) {
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
                    getSpeechInput(control);

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
                        if (control.equals("PopUp")) {
                            popUpSpeechInputEn(voiceInput);
                        }
                        if (control.equals("Activity")) {
                            activitySpeechInputEn(voiceInput);
                        }
                        break;
                    case "romanian":
                        if (control.equals("PopUp")) {
                            popUpSpeechInputRou(voiceInput);
                        }
                        if (control.equals("Activity")) {
                            activitySpeechInputRou(voiceInput);
                        }
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

    private void popUpSpeechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "english":
            case "English":
                if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.engLanguageRadioButton) {
                    engRadioButton.performClick();
                } else {
                    speak(getString(R.string.englishAlsoSelectedAudioLogEditEn), QUEUE_ADD);
                }
                break;
            case "romanian":
            case "Romanian":
                romRadioButton.performClick();
                break;
            case "microphone":
            case "Microphone":
                extendedSwitchMicrophone.performClick();
                break;
            case "speaker":
            case "Speaker":
                extendedSwitchSpeaker.performClick();
                break;
            case "next":
            case "Next":
                continueButtonPopUp.performClick();
                return;
            case "describe":
            case "described":
                speak(describePopUpAudio, QUEUE_ADD);
                return;

            default:
                invalidVoiceInput("PopUp");
        }

    }

    private void popUpSpeechInputRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "engleză":
            case "Engleza":
                engRadioButton.performClick();
                break;
            case "română":
            case "Română":
                if (chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.romLanguageRadioButton) {
                    romRadioButton.performClick();

                } else {
                    speak(getString(R.string.romanianAlsoSelectedAudioLogEditRou), QUEUE_ADD);

                }
                break;
            case "microfon":
            case "Microfon":
                extendedSwitchMicrophone.performClick();
                break;
            case "difuzor":
            case "Difuzor":
                extendedSwitchSpeaker.performClick();
                break;
            case "continuă":
            case "Continuă":
                continueButtonPopUp.performClick();
                return;
            case "descrie":
            case "descriere":
                speak(describePopUpAudio, QUEUE_ADD);
                return;

            default:
                invalidVoiceInput("PopUp");
        }

    }

    private void invalidVoiceInput(String currentScreen) {
        Toast.makeText(this, invalidCommandToastAudio, Toast.LENGTH_SHORT).show();
        switch (currentScreen) {
            case "PopUp":
                speak(invalidCommandToastAudio, QUEUE_ADD);
                break;
            case "Activity":
                checkOptions(invalidCommandToastAudio, currentScreen);
                break;

        }

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
            String email = usefulDataExtract(voiceInput, 10);
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput("Activity");
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            emailInput.setText(email);
            checkOptions(mailSetAudio + SPACESTRING + email + "!", "Activity");

        } else {
            return false;

        }
        return true;

    }

    private boolean checkSetEmailCommandRou(String voiceInput) {
        String platform;
        boolean rule;

        rule = voiceInput.startsWith("setează email ");
        if (rule) {
            String email = usefulDataExtract(voiceInput, 14);
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput("Activity");
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            emailInput.setText(email);
            checkOptions(mailSetAudio + SPACESTRING + email + "!", "Activity");

        } else {
            return false;

        }
        return true;

    }

    private boolean checkSetPasswordCommandEn(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("set password ");
        if (rule) {
            String password = usefulDataExtract(voiceInput, 13);
            passwordInput.setText(password);
            checkOptions(passwordSetAudio, "Activity");

        } else {
            return false;

        }
        return true;

    }

    private boolean checkSetPasswordCommandRou(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("setează parolă ");
        if (rule) {
            String password = usefulDataExtract(voiceInput, 15);
            passwordInput.setText(password);
            checkOptions(passwordSetAudio, "Activity");

        } else {
            return false;

        }
        return true;

    }

    private boolean checkSendMailCommandEn(String voiceInput) {
        String platform;
        short length = 0;
        boolean rule = false;
        if (voiceInput.startsWith("send mail ")) {
            rule = true;
            length = 10;

        }
        if (voiceInput.startsWith("sendmail ")) {
            rule = true;
            length = 9;

        }

        if (rule) {
            String email = usefulDataExtract(voiceInput, length);
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput("Activity");
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            forgotPasswordEmailInput.setText(email);
            sendMailButton.performClick();

        } else {
            return false;

        }
        return true;

    }

    private boolean checkSendMailCommandRou(String voiceInput) {
        String platform;

        if (voiceInput.startsWith("trimite mail ")) {
            String email = usefulDataExtract(voiceInput, 13);
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput("Activity");
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            forgotPasswordEmailInput.setText(email);
            sendMailButton.performClick();

        } else {
            return false;

        }
        return true;

    }

    private String usefulDataExtract(String voiceInput, int length) {
        String usefulData = voiceInput.substring(length);
        usefulData = usefulData.replaceAll("\\s+", "");
        return usefulData;


    }

    private void activitySpeechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkSetEmailCommandEn(voiceInput)) {
            return;

        }
        if (checkSetPasswordCommandEn(voiceInput)) {
            return;

        }
        if (checkSendMailCommandEn(voiceInput)) {
            return;

        }

        switch (voiceInput) {
            case "login":
                logInButton.performClick();
                return;
            case "create account":
                createAccountButton.performClick();
                return;
            case "describe":
            case "described":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeCommandsAudio, QUEUE_ADD);

                }
                return;
            default:
                invalidVoiceInput("Activity");

        }

    }

    private void activitySpeechInputRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if (checkSetEmailCommandRou(voiceInput)) {
            return;

        }
        if (checkSetPasswordCommandRou(voiceInput)) {
            return;

        }
        if (checkSendMailCommandRou(voiceInput)) {
            return;

        }

        switch (voiceInput) {
            case "login":
                logInButton.performClick();
                return;
            case "creează cont":
                createAccountButton.performClick();
                return;
            case "descrie":
            case "descriere":
                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(describeAudio, QUEUE_ADD);

                }
                return;
            default:
                invalidVoiceInput("Activity");

        }

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (textToSpeech != null) {
                    Log.d("Main","connectionListener");
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        connected();
                    } else {
                        lossConnection();

                    }
                }

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
        switch (currentScreen) {
            case "Nothing":
            case "PopUp":
                speak(connectedToastAudio, QUEUE_ADD);
                break;
            case "Activity":
                checkOptions(connectedToastAudio, currentScreen);
                break;
            default:
                break;

        }

    }

    private void lossConnection() {
        if (!optionList.get(EXMIC).isValue()) {
            connectionStatus = false;
            Toast.makeText(getApplicationContext(), connectionLostToastAudio, Toast.LENGTH_SHORT).show();
            switch (currentScreen) {
                case "Nothing":
                case "PopUp":
                    speak(connectionLostToastAudio, QUEUE_ADD);
                    break;
                case "Activity":
                    if (optionList.get(EXSPEAKER).isValue()) {
                        speak(connectionLostToastAudio, QUEUE_ADD);

                    }
                    break;
                default:
                    break;
            }

        }

    }

    private void checkOptions(String feedback, String screen) {
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(feedback, QUEUE_ADD);

        } else {
            if (optionList.get(EXMIC).isValue()) {
                getSpeechInput(screen);

            }

        }

    }

}