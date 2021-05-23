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
import android.widget.Switch;
import android.widget.Toast;

import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.LoggedUserData.*;

public class GameSettingsActivity extends AppCompatActivity {
    private Switch sportCategorySwitch;
    private Switch geographyCategorySwitch;
    private Switch mathsCategorySwitch;
    private Switch othersCategorySwitch;

    private Button playButton;
    private String oneCategoryToast;
    private Intent speechIntent;
    private SpeechRecognizer speechRecognizer;

    String invalidInputToast;
    Locale selectedLanguage;

    boolean currentState;

    private TextToSpeech textToSpeech;
    private boolean connectionListenerStatus = false;

    String categorySelectedAudio, categoryDeselectedAudio, describeAudio, describeCommandsAudio, connectedToastAudio, connectionLostToastAudio, invalidCommandToastAudio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
        initialize();
        initializeViews();
        switchesListeners();

    }

    private void initialize() {
        currentActivity = this;
        speechInitialize();
        setTextToSpeechListener();

    }

    private void initializeViews() {
        sportCategorySwitch = findViewById(R.id.sportCategorySwitch);
        geographyCategorySwitch = findViewById(R.id.geographyCategorySwitch);
        mathsCategorySwitch = findViewById(R.id.mathsCategorySwitch);
        othersCategorySwitch = findViewById(R.id.othersCategorySwitch);
        sportCategorySwitch.setChecked(optionList.get(SPORT).isValue());
        geographyCategorySwitch.setChecked(optionList.get(GEO).isValue());
        mathsCategorySwitch.setChecked(optionList.get(MATHS).isValue());
        othersCategorySwitch.setChecked(optionList.get(OTHERS).isValue());
        playButton = findViewById(R.id.playBtn);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage() {
        geographyCategorySwitch.setText(R.string.geographySwitchSettingsEn);
        mathsCategorySwitch.setText(R.string.mathsSwitchSettingsEn);
        othersCategorySwitch.setText(R.string.othersSwitchSettingsEn);
        playButton.setText(R.string.playButtonSettingsEn);
        oneCategoryToast = getString(R.string.oneCategoryToastSettingsEn);
        invalidInputToast = getString(R.string.invalidInputToastPlayEn);
        categorySelectedAudio = getString(R.string.categorySelectedAudioSettingsEn);
        categoryDeselectedAudio = getString(R.string.categoryDeselectedAudioSettingsEn);
        describeAudio = getString(R.string.describeAudioSettingsEn);
        describeCommandsAudio = getString(R.string.describeCommandsAudioSettingsEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);
        selectedLanguage = Locale.ENGLISH;

    }

    private void setViewForRomanianLanguage() {
        geographyCategorySwitch.setText(R.string.geographySwitchSettingsRou);
        mathsCategorySwitch.setText(R.string.mathsSwitchSettingsRou);
        othersCategorySwitch.setText(R.string.othersSwitchSettingsRou);
        playButton.setText(R.string.playButtonSettingsRou);
        oneCategoryToast = getString(R.string.oneCategoryToastSettingsRou);
        invalidInputToast = getString(R.string.invalidInputToastPlayRou);
        categorySelectedAudio = getString(R.string.categorySelectedAudioSettingsRou);
        categoryDeselectedAudio = getString(R.string.categoryDeselectedAudioSettingsRou);
        describeAudio = getString(R.string.describeAudioSettingsRou);
        describeCommandsAudio = getString(R.string.describeCommandsAudioSettingsRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioRou);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioRou);
        selectedLanguage = Locale.getDefault();

    }

    private void chooseLanguage() {
        switch (LoggedUserData.language) {
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


    private void switchListenerTemplate(int option, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        optionList.get(option).setValue(value);
        editor.putString(optionList.get(option).getName(), String.valueOf(value));
        editor.apply();

    }

    private void switchesListeners() {
        sportCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(SPORT, isChecked);
            audioFeedbackForSwitchChanges(sportCategorySwitch);

        });

        geographyCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(GEO, isChecked);
            audioFeedbackForSwitchChanges(geographyCategorySwitch);

        });

        mathsCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(MATHS, isChecked);
            audioFeedbackForSwitchChanges(mathsCategorySwitch);

        });

        othersCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(OTHERS, isChecked);
            audioFeedbackForSwitchChanges(othersCategorySwitch);

        });

    }

    private void audioFeedbackForSwitchChanges(Switch s) {
        currentState = s.isChecked();
        if (currentState) {
            checkOptions(s.getText() + SPACESTRING + categorySelectedAudio);
        } else {
            checkOptions(s.getText() + SPACESTRING + categoryDeselectedAudio);

        }

    }

    public void openPlayActivity(View view) {
        speechRecognizer.destroy();
        if (!optionList.get(SPORT).isValue() && !optionList.get(GEO).isValue() && !optionList.get(MATHS).isValue() && !optionList.get(OTHERS).isValue()) {
            Toast.makeText(getBaseContext(), oneCategoryToast, Toast.LENGTH_SHORT).show();
            if (optionList.get(MIC).isValue()) {
                getSpeechInput();
            }
            return;

        }

        Intent intent = new Intent(getBaseContext(), PlayActivity.class);
        startActivity(intent);
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
                    if (optionList.get(EXMIC).isValue() || optionList.get(MIC).isValue()) {
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
        if (textToSpeech == null) {
            Log.d("GameSettings", "NULL SPEAK OBJECT");

        }
        textToSpeech.speak(text, queueMode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

    }


    private void speechInitialize() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//deschide o activitate ce solicita utilizatorului sa vorbeasca si trimite mesajul catre un SpeechRecognizer.
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
                Log.d("error", String.valueOf(error));
                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    speechRecognizer.destroy();
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
                String response = result.get(0);
                Log.d("GameSettings", response);
                switch (LoggedUserData.language) {
                    case "english":
                        afterSpeechInputEn(response);
                        break;
                    case "romanian":
                        afterSpeechInputRou(response);
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

    private void changeSwitchOption(Switch s) {
        speechRecognizer.destroy();
        currentState = s.isChecked();
        currentState = !currentState;
        s.setChecked(currentState);

    }

    private void afterSpeechInputEn(String response) {
        switch (response) {
            case "Sport":
            case "sport":
                changeSwitchOption(sportCategorySwitch);
                break;
            case "Geography":
            case "geography":
                changeSwitchOption(geographyCategorySwitch);
                break;
            case "Mathematics":
            case "mathematics":
                changeSwitchOption(mathsCategorySwitch);
                break;
            case "Others":
            case "others":
                changeSwitchOption(othersCategorySwitch);
                break;
            case "Play":
            case "play":
                playButton.performClick();
                break;
            case "Status":
            case "status":
                optionsStatus();
                break;
            case "described":
            case "describe":
                checkOptions(describeCommandsAudio);
                break;
            default:
                invalidVoiceInput();

        }

    }

    private void afterSpeechInputRou(String response) {
        switch (response) {
            case "Sport":
            case "sport":
                changeSwitchOption(sportCategorySwitch);
                break;
            case "Geografie":
            case "geografie":
                changeSwitchOption(geographyCategorySwitch);
                break;
            case "Mate":
            case "mate":
                changeSwitchOption(mathsCategorySwitch);
                break;
            case "Altele":
            case "altele":
                changeSwitchOption(othersCategorySwitch);
                break;
            case "Joacă":
            case "joacă":
                playButton.performClick();
                break;
            case "Status":
            case "status":
                optionsStatus();
                break;
            case "descriere":
            case "descrie":
                checkOptions(describeCommandsAudio);
                break;
            default:
                invalidVoiceInput();

        }

    }

    private void optionsStatus() {
        String sportSwitchStatus = setOptionText(sportCategorySwitch);
        String geographySwitchStatus = setOptionText(geographyCategorySwitch);
        String mathsSwitchStatus = setOptionText(mathsCategorySwitch);
        String othersSwitchStatus = setOptionText(othersCategorySwitch);

        if (optionList.get(EXSPEAKER).isValue()) {
            speak("Sport Switch:" + sportSwitchStatus, QUEUE_ADD);
            speak("Geography Switch:" + geographySwitchStatus, QUEUE_ADD);
            speak("Maths Switch:" + mathsSwitchStatus, QUEUE_ADD);
            speak("Others Switch:" + othersSwitchStatus, QUEUE_ADD);

        } else {
            if (optionList.get(EXMIC).isValue()) {
                getSpeechInput();

            }

        }

    }

    private String setOptionText(Switch s) {
        if (s.isChecked()) {
            return "On";

        }

        return "Off";

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && currentActivity instanceof GameSettingsActivity) {
                    Log.d("GameSettings", "connectionListener");
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


    private void invalidVoiceInput() {
        Toast.makeText(this, invalidInputToast, Toast.LENGTH_SHORT).show();
        checkOptions(invalidCommandToastAudio);

    }

    private void checkOptions(String feedback) {
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(feedback, QUEUE_ADD);

        } else {
            if (optionList.get(EXMIC).isValue() || optionList.get(MIC).isValue()) {
                getSpeechInput();

            }

        }

    }

}