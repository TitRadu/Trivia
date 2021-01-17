package com.example.triviaapp.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;

import java.util.ArrayList;
import java.util.Locale;

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

    String geographySpeechOption, mathsSpeechOption, othersSpeechOption, playSpeechOption;
    String invalidInputToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
        initializeViews();
        switchesListeners();

    }

    private void initializeViews(){
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

    private void setViewForEnglishLanguage(){
        geographyCategorySwitch.setText(R.string.geographySwitchSettingsEn);
        mathsCategorySwitch.setText(R.string.mathsSwitchSettingsEn);
        othersCategorySwitch.setText(R.string.othersSwitchSettingsEn);
        playButton.setText(R.string.playButtonSettingsEn);
        oneCategoryToast = getString(R.string.oneCategoryToastSettingsEn);
        invalidInputToast = getString(R.string.invalidInputToastPlayEn);

    }

    private void setSpeechOptionsForEnglishLanguage(){
        geographySpeechOption = "geography";
        mathsSpeechOption = "maths";
        othersSpeechOption = "others";
        playSpeechOption = "play";

    }


    private void setViewForRomanianLanguage(){
        geographyCategorySwitch.setText(R.string.geographySwitchSettingsRou);
        mathsCategorySwitch.setText(R.string.mathsSwitchSettingsRou);
        othersCategorySwitch.setText(R.string.othersSwitchSettingsRou);
        playButton.setText(R.string.playButtonSettingsRou);
        oneCategoryToast = getString(R.string.oneCategoryToastSettingsRou);
        invalidInputToast = getString(R.string.invalidInputToastPlayRou);

    }

    private void setSpeechOptionsForRomanianLanguage(){
        geographySpeechOption = "geografie";
        mathsSpeechOption = "mate";
        othersSpeechOption = "altele";
        playSpeechOption = "joacă";

    }

    private void chooseLanguage(){
        switch (LoggedUserData.language){
            case "english":
                setViewForEnglishLanguage();
                setSpeechOptionsForEnglishLanguage();
                break;
            case "romanian":
                setViewForRomanianLanguage();
                setSpeechOptionsForRomanianLanguage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
        }

    }


    private void switchListenerTemplate(int option, boolean value){
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        optionList.get(option).setValue(value);
        editor.putString(optionList.get(option).getName(),String.valueOf(value));
        editor.apply();

    }

    private void switchesListeners(){
        sportCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(SPORT, isChecked);

        });

        geographyCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(GEO, isChecked);

        });

        mathsCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(MATHS, isChecked);

        });

        othersCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(OTHERS, isChecked);

        });

    }

    public void openPlayActivity(View view){
        if(!optionList.get(SPORT).isValue() && !optionList.get(GEO).isValue() && !optionList.get(MATHS).isValue() && !optionList.get(OTHERS).isValue()){
            Toast.makeText(getBaseContext(),oneCategoryToast,Toast.LENGTH_SHORT).show();
            return;

        }

        Intent intent = new Intent(getBaseContext(), PlayActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

  /*  private void speechInitialize(){
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//deschide o activitate ce solicita utilizatorului sa vorbeasca si trimite mesajul catre un SpeechRecognizer.
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        switch (language){
            case "english":speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());break;
            case "romanian":speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);break;

        }

    }

    private void getSpeechInput() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
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
                if(error == SpeechRecognizer.ERROR_NO_MATCH) {
                    speechRecognizer.destroy();
                    getSpeechInput();

                }

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String response = result.get(0);
                afterSpeechInput(response);

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

        });

    }

    private void afterSpeechInput(String response){
        switch (response){
            case "sport":
                sportCategorySwitch.callOnClick();
                break;
            case geographySpeechOption:
                geographyCategorySwitch.callOnClick();
                break;
            case mathsSpeechOption:
                mathsCategorySwitch.callOnClick();
                break;
            case othersSpeechOption:
                othersCategorySwitch.callOnClick();
                break;
            case playSpeechOption:
                playButton.performClick();
                break;
            default:Toast.makeText(this,invalidInputToast, Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();

        }

    }
    */
}