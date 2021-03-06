package com.example.triviaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.triviaapp.LoggedUserData.EXMIC;
import static com.example.triviaapp.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.LoggedUserData.MIC;
import static com.example.triviaapp.LoggedUserData.SPEAKER;
import static com.example.triviaapp.LoggedUserData.language;
import static com.example.triviaapp.LoggedUserData.loggedSuperPowerCorrectAnswer;
import static com.example.triviaapp.LoggedUserData.loggedSuperPowerFiftyFifty;
import static com.example.triviaapp.LoggedUserData.onResumeFromAnotherActivity;
import static com.example.triviaapp.LoggedUserData.optionList;
import static com.example.triviaapp.LoggedUserData.userNameList;

public class MainActivity extends AppCompatActivity {
    public static final Integer RECORD_AUDIO = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser loggedUser;

    private EditText emailInput, passwordInput, forgotPasswordEmailInput;

    private LinearLayout forgotPasswordLayout;

    Button logInButton, createAccountButton, sendMailButton;
    TextView forgotPasswordTextView;
    String emptyMailToast, emptyPasswordToast, successDataToast, wrongDataToast, successSendMailToast, wrongMailToast, audioGrantedToast, audioDeniedToast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        initializeMicrophoneStatusAndCategoriesOptions();
        initializeViews();
        initializeUserNameList();
        initializeLoggedUser();
        setActivityStartPopUp();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onResumeFromAnotherActivity) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            chooseLanguage();
            if (optionList.get(EXSPEAKER).isValue()) {
                textToSpeechListener("Now, you are in Main Activity!", "Activity");

            }else{
                if(optionList.get(EXMIC).isValue()){
                    getSpeechInput("Activity");

                }

            }
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
        passwordInput.setHint(R.string.passwordHintLogRegEditEn);
        logInButton.setText(R.string.logInButtonLogRegEn);
        createAccountButton.setText(R.string.createButtonLogRegEn);
        forgotPasswordTextView.setText(R.string.forgotPasswordTextViewLogEn);
        sendMailButton.setText(R.string.sendMailButtonLogEn);
        emptyMailToast = getString(R.string.emptyMailToastLogRegEn);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEditEn);
        successDataToast = getString(R.string.successDataToastLogEn);
        wrongDataToast = getString(R.string.wrongDataToastLogEn);
        successSendMailToast = getString(R.string.successSendMailToastLogEn);
        wrongMailToast = getString(R.string.wrongMailToastLogEn);
        audioGrantedToast = getString(R.string.audioGrantedToastLogEn);
        audioDeniedToast = getString(R.string.audioDeniedToastLogEn);

    }

    private void setViewForRomanianLanguage() {
        passwordInput.setHint(R.string.passwordHintLogRegEditRou);
        logInButton.setText(R.string.logInButtonLogRegRou);
        createAccountButton.setText(R.string.createButtonLogRegRou);
        forgotPasswordTextView.setText(R.string.forgotPasswordTextViewLogRou);
        sendMailButton.setText(R.string.sendMailButtonLogRou);
        emptyMailToast = getString(R.string.emptyMailToastLogRegRou);
        emptyPasswordToast = getString(R.string.emptyPasswordToastLogRegEditRou);
        successDataToast = getString(R.string.successDataToastLogRou);
        wrongDataToast = getString(R.string.wrongDataToastLogRou);
        successSendMailToast = getString(R.string.successSendMailToastLogRou);
        wrongMailToast = getString(R.string.wrongMailToastLogRou);
        audioGrantedToast = getString(R.string.audioGrantedToastLogRou);
        audioDeniedToast = getString(R.string.audioDeniedToastLogRou);

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

    private void initializeLoggedUser() {
        loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        if (loggedUser != null) {
            String data = prefs.getString("millis", "Key not found!");
            LoggedUserData.millis = Long.parseLong(data);
            updateUI();

        }

    }
    
    private void setActivityStartPopUp(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            textToSpeechListener("Welcome to Trivia Audio Permission!", "Nothing");
            checkPermission();

        } else {
            if (!checkIfCompletedOptionsPopUp()) {
                textToSpeechListener("Welcome to Trivia!", "PopUp");
                chooseOptionsPopUp();
            } else {
                if (optionList.get(EXSPEAKER).isValue()) {
                    textToSpeechListener("Now, you are in Main Activity!", "Activity");

                }

            }

        }
        
    }

    private boolean inputCheck(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, emptyMailToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                textToSpeechListener("Introduce an email!", "Activity");

            }
            return false;

        }

        if (password.isEmpty()) {
            Toast.makeText(this, emptyPasswordToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                textToSpeechListener("Introduce a password!", "Activity");

            }
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
        speechRecognizer.stopListening();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!inputCheck(email, password)) {
            if (!optionList.get(EXSPEAKER).isValue()) {
                if (optionList.get(EXMIC).isValue()) {
                    getSpeechInput("Activity");

                }

            }
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
                            Toast.makeText(getBaseContext(), wrongDataToast, Toast.LENGTH_SHORT).show();
                            if (optionList.get(EXSPEAKER).isValue()) {
                                textToSpeechListener(wrongDataToast, "Activity");

                            } else {
                                if (optionList.get(EXMIC).isValue()) {
                                    getSpeechInput("Activity");
                                }

                            }

                        }

                    }
                });

    }

    private void updateUI() {
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
        String email = forgotPasswordEmailInput.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, emptyMailToast, Toast.LENGTH_SHORT).show();
            getSpeechInput("Activity");
            return;

        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), successSendMailToast, Toast.LENGTH_SHORT).show();
                    if (optionList.get(EXSPEAKER).isValue()) {
                        textToSpeechListener(successSendMailToast, "Activity");

                    } else {
                        getSpeechInput("Activity");

                    }

                } else {
                    Toast.makeText(getApplicationContext(), wrongMailToast, Toast.LENGTH_SHORT).show();
                    if (optionList.get(EXSPEAKER).isValue()) {
                        textToSpeechListener(wrongMailToast, "Activity");

                    } else {
                        getSpeechInput("Activity");

                    }

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
                Toast.makeText(this, audioGrantedToast, Toast.LENGTH_SHORT).show();
                if (!checkIfCompletedOptionsPopUp()) {
                    textToSpeechListener("Welcome to Trivia!", "PopUp");
                    chooseOptionsPopUp();
                } else {
                    textToSpeechListener("Now, you are in Main Activity!", "Activity");

                }


            } else {
                Toast.makeText(this, audioDeniedToast, Toast.LENGTH_SHORT).show();
                if (!checkIfCompletedOptionsPopUp()) {
                    textToSpeechListener("Welcome to Trivia!", "PopUp");
                    chooseOptionsPopUp();
                } else {
                    textToSpeechListener("Now, you are in Main Activity!", "Activity");

                }

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
                    textToSpeechListener("English language was selected!", "PopUp");
                    setViewForEnglishLanguage();
                    LoggedUserData.language = "english";
                    editor.putString("language", "english");
                    editor.apply();
                    break;
                case R.id.romLanguageRadioButton:
                    speechRecognizer.destroy();
                    textToSpeechListener("Romanian language was selected!", "PopUp");
                    setViewForRomanianLanguage();
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
            Toast.makeText(this, "Select a language!", Toast.LENGTH_SHORT).show();
            textToSpeechListener("Select a language!", "PopUp");
            return;

        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("startApp", "true");
        editor.apply();
        dialog.dismiss();
        if (optionList.get(EXSPEAKER).isValue()) {
            textToSpeechListener("Now, you are in Main Activity!", "Activity");

        } else {
            if (optionList.get(EXMIC).isValue()) {
                getSpeechInput("Activity");
            }

        }

    }

    private void interactionChooseListener() {
        extendedSwitchMicrophone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speechRecognizer.destroy();
            if (isChecked) {
                textToSpeechListener("Microphone option was selected!", "PopUp");

            } else {
                textToSpeechListener("Microphone option was deselected!", "PopUp");

            }
            SharedPreferences.Editor editor = prefs.edit();
            optionList.get(EXMIC).setValue(isChecked);
            editor.putString("exMic", String.valueOf(isChecked));
            editor.apply();

        });
        extendedSwitchSpeaker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speechRecognizer.destroy();
            if (isChecked) {
                textToSpeechListener("Speaker option was selected!", "PopUp");

            } else {
                textToSpeechListener("Speaker option was deselected!", "PopUp");

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
            destroySpeaker();
            textToSpeechListener("To restart presentation say describe!", "PopUp");
        });

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

    private void textToSpeechListener(String text, String control) {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                setProgressListener(control);
                int result = textToSpeech.setLanguage(selectedLanguage);
                textToSpeech.setPitch(1);
                textToSpeech.setSpeechRate(0.75f);
                speak(text);


                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getBaseContext(), "Language not supported!", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(getBaseContext(), "Initialization failed!", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void setProgressListener(String control) {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                Runnable runnable = () -> {
                    if (control.equals("PopUp")) {
                        setOptionsPopUpViewsEnableState(true);
                        getSpeechInput("PopUp");

                    }
                    if (control.equals("Activity")) {
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

    private void speak(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

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
                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    if (control.equals("PopUp")) {
                        getSpeechInput("PopUp");

                    }
                    if (control.equals("Activity")) {
                        getSpeechInput("Activity");

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
                            popUpSpeechInputEn(voiceInput);
                        }
                        if (control.equals("Activity")) {
                            activitySpeechInputEn(voiceInput);
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
                if(chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.engLanguageRadioButton){
                    engRadioButton.performClick();
                }else{
                    textToSpeechListener("English language is also selected!", "PopUp");
                }
                break;
            case "romanian":
            case "Romanian":
                if(chooseLanguageRadioGroup.getCheckedRadioButtonId() != R.id.romLanguageRadioButton){
                    romRadioButton.performClick();
                }else{
                    textToSpeechListener("Romanian language is also selected!", "PopUp");
                }
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
                textToSpeechListener("Welcome to Trivia!", "PopUp");
                return;

            default:
                Toast.makeText(this, "Invalid command!", Toast.LENGTH_SHORT).show();
                textToSpeechListener("Invalid command!", "PopUp");
        }

    }

    private void invalidVoiceInput(String currentScreen) {
        Toast.makeText(this, "Invalid command!", Toast.LENGTH_SHORT).show();
        switch(currentScreen){
            case "PopUp":textToSpeechListener("Invalid command!", currentScreen);break;
            case "Activity":
                if(optionList.get(EXSPEAKER).isValue()){
                    textToSpeechListener("Invalid command!", currentScreen);

                }else {
                    if(optionList.get(EXMIC).isValue()){
                        getSpeechInput(currentScreen);

                    }

                }
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

    private boolean checkSetEmailCommand(String voiceInput) {
        String platform;
        boolean rule;

        rule = voiceInput.startsWith("set email ");
        if (rule) {
            String email = voiceInput.substring(9);
            email = email.replaceAll("\\s+", "");
            if ((platform = getPlatformFromVoiceInput(email)) == null) {
                invalidVoiceInput("Activity");
                return false;
            }
            email = email.replaceAll(platform, "@" + platform + ".com");
            emailInput.setText(email);
            if (optionList.get(EXSPEAKER).isValue()) {
                textToSpeechListener("Mail was set to " + email + "!", "Activity");
                return true;

            }
            getSpeechInput("Activity");


        } else {
            return false;

        }
        return true;

    }

    private boolean checkSetPasswordCommand(String voiceInput) {
        boolean rule;
        rule = voiceInput.startsWith("set password ");
        if (rule) {
            String password = voiceInput.substring(13);
            password = password.replaceAll("\\s+", "");
            passwordInput.setText(password);
            if (optionList.get(EXSPEAKER).isValue()) {
                textToSpeechListener("Password was set!", "Activity");
                return true;

            }
            getSpeechInput("Activity");


        } else {
            return false;

        }
        return true;

    }

    private boolean checkSendMailCommand(String voiceInput) {
        String platform;
        boolean rule;
        rule = (voiceInput.startsWith("send mail ") || voiceInput.startsWith("sendmail "));
        if (rule) {
            String email = voiceInput.substring(10);
            email = email.replaceAll("\\s+", "");
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

    private void activitySpeechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();

        if(checkSetEmailCommand(voiceInput)){
            return;

        }
        if(checkSetPasswordCommand(voiceInput)){
            return;

        }
        if(checkSendMailCommand(voiceInput)){
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
                    textToSpeechListener("Activity description!", "Activity");

                }
                return;
            default:
                invalidVoiceInput("Activity");

        }

    }

}