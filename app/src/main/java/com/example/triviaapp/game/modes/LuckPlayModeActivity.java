package com.example.triviaapp.game.modes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.data.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.GameActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.data.LoggedUserData.EXMIC;
import static com.example.triviaapp.data.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.data.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.data.LoggedUserData.connectionStatus;
import static com.example.triviaapp.data.LoggedUserData.currentActivity;
import static com.example.triviaapp.data.LoggedUserData.optionList;

public class LuckPlayModeActivity extends AppCompatActivity {
    TextView countTextView, fiftyCountTextTextView, fiftyCountValueTextView, rightCountTextTextView, rightCountValueTextView;
    Button firstOptionButton, secondOptionButton, thirdOptionButton, fourthOptionButton, collectButton, collectButtonPopUp;
    String collectQuestionTextViewPopUpTextAudioString, lostPrizeTextViewPopUpTextAudioString, collectButtonPopUpTextString, lostPrizeButtonPopUpTextString,
    wonFFAudio, wonRAAudio, noneAnswerAudio, wrongAnswerAudio, fiftyFiftyNumberAudio, rightAnswerNumberAudio, shortRAButtonTextString, noneAnswerButtonTextString, wrongAnswerButtonTextString, describeAudio, describeCommandsAudio, connectedToastAudio, lostConnectionToastAudio, invalidCommandToastAudio;
    ImageView xImageViewPopUp;

    int count = 1;
    int rightAnswerCount = 0;
    int fiftyFiftyCount = 0;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    String voiceInput = null;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    Locale selectedLanguage;

    private TextToSpeech textToSpeech;
    private boolean connectionListenerStatus = false;

    private boolean viewsEnableStatus = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luck_play_mode);
        initialize();
        initializeViews();
        setOnClickListeners();

    }

    private void initialize() {
        currentActivity = this;
        speechInitialize();
        setTextToSpeechListener();

    }

    private void initializeViews() {
        countTextView = findViewById(R.id.countTextView);
        fiftyCountTextTextView = findViewById(R.id.fiftyCountTextTextView);
        fiftyCountValueTextView = findViewById(R.id.fiftyCountValueTextView);
        rightCountTextTextView = findViewById(R.id.rightCountTextTextView);
        rightCountValueTextView = findViewById(R.id.rightCountValueTextView);
        firstOptionButton = findViewById(R.id.firstOptionButton);
        secondOptionButton = findViewById(R.id.secondOptionButton);
        thirdOptionButton = findViewById(R.id.thirdOptionButton);
        fourthOptionButton = findViewById(R.id.fourthOptionButton);
        collectButton = findViewById(R.id.collectButton);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage() {
        rightCountTextTextView.setText(R.string.rightAnswerTextAudioLuckEn);
        collectButton.setText(R.string.collectButtonTextLuckEn);
        collectQuestionTextViewPopUpTextAudioString = getString(R.string.collectQuestionTextAudioLuckEn);
        lostPrizeTextViewPopUpTextAudioString = getString(R.string.lostPrizeTextAudioLuckEn);
        collectButtonPopUpTextString = getString(R.string.collectButtonTextLuckEn);
        lostPrizeButtonPopUpTextString = getString(R.string.exitButtonTextLuckEn);
        describeAudio = getString(R.string.describeAudioLuckEn);
        describeCommandsAudio = getString(R.string.describeAudioLuckEn);
        wonFFAudio = getString(R.string.wonFFAudioPlayLuckEn);
        wonRAAudio = getString(R.string.wonRAAudioPlayLuckEn);
        noneAnswerAudio = getString(R.string.noneAnswerAudioLuckEn);
        wrongAnswerAudio = getString(R.string.wrongAnswerAudioPlayLuckEn);
        shortRAButtonTextString = getString(R.string.rightAnswerShortTextLuckEn);
        noneAnswerButtonTextString = getString(R.string.noneAnswerTextLuckEn);
        wrongAnswerButtonTextString = getString(R.string.wrongAnswerTextLuckEn);
        fiftyFiftyNumberAudio = getString(R.string.fiftyFiftyNumberAudioLuckEn);
        rightAnswerNumberAudio = getString(R.string.rightAnswerTextAudioLuckEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        lostConnectionToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);

    }


    private void setViewForRomanianLanguage() {
        rightCountTextTextView.setText(R.string.rightAnswerTextAudioLuckRou);
        collectButton.setText(R.string.collectButtonTextLuckRou);
        collectQuestionTextViewPopUpTextAudioString = getString(R.string.collectQuestionTextAudioLuckRou);
        lostPrizeTextViewPopUpTextAudioString = getString(R.string.lostPrizeTextAudioLuckRou);
        collectButtonPopUpTextString = getString(R.string.collectButtonTextLuckRou);
        lostPrizeButtonPopUpTextString = getString(R.string.exitButtonTextLuckRou);
        describeAudio = getString(R.string.describeAudioLuckRou);
        wonFFAudio = getString(R.string.wonFFAudioPlayLuckRou);
        wonRAAudio = getString(R.string.wonRAAudioPlayLuckRou);
        noneAnswerAudio = getString(R.string.noneAnswerAudioLuckRou);
        wrongAnswerAudio = getString(R.string.wrongAnswerAudioPlayLuckRou);
        shortRAButtonTextString = getString(R.string.rightAnswerShortTextLuckRou);
        noneAnswerButtonTextString = getString(R.string.noneAnswerTextLuckRou);
        wrongAnswerButtonTextString = getString(R.string.wrongAnswerTextLuckRou);
        fiftyFiftyNumberAudio = getString(R.string.fiftyFiftyNumberAudioLuckRou);
        rightAnswerNumberAudio = getString(R.string.rightAnswerTextAudioLuckRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        lostConnectionToastAudio = getString(R.string.connectionLostToastAudioRou);
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

    private void setOnClickListeners() {
        collectButton.setOnClickListener(v -> collectPopUp("Collect"));

    }

    public void setOptionButtonsListener(View view) {
        setEnabledForOptionButtons(false);

        Random rand = new Random();
        List<Integer> randomOptionList = new ArrayList<>();
        int r;

        while (randomOptionList.size() < 4) {
            r = rand.nextInt(4);
            if (!randomOptionList.contains(r)) {
                randomOptionList.add(r);

            }

        }


        int bonus = 2;
        switch (view.getId()) {
            case R.id.firstOptionButton:
                bonus = randomOptionList.get(0);
                break;
            case R.id.secondOptionButton:
                bonus = randomOptionList.get(1);
                break;

            case R.id.thirdOptionButton:
                bonus = randomOptionList.get(2);
                break;

            case R.id.fourthOptionButton:
                bonus = randomOptionList.get(3);
                break;

        }
        Log.d("option", String.valueOf(bonus));
        switch (bonus) {
            case 0:
                speechRecognizer.destroy();
                ((Button) view).setText(shortRAButtonTextString);
                checkOptions(wonRAAudio, "Base");
                rightAnswerCount++;
                rightCountValueTextView.setText(String.valueOf(rightAnswerCount));
                break;
            case 1:
                speechRecognizer.destroy();
                ((Button) view).setText("50/50");
                checkOptions(wonFFAudio, "Base");
                fiftyFiftyCount++;
                fiftyCountValueTextView.setText(String.valueOf(fiftyFiftyCount));
                break;
            case 2:
                speechRecognizer.destroy();
                ((Button) view).setText(noneAnswerButtonTextString);
                checkOptions(noneAnswerAudio, "Base");
                break;
            case 3:
                speechRecognizer.destroy();
                ((Button) view).setText(wrongAnswerButtonTextString);
                checkOptions(wrongAnswerAudio, "Base");
                delay(3000, "Wrong");
                return;

        }

        delay(3000, "Collect");

    }

    private void setEnabledForOptionButtons(boolean status) {
        viewsEnableStatus = status;
        collectButton.setEnabled(status);
        firstOptionButton.setEnabled(status);
        secondOptionButton.setEnabled(status);
        thirdOptionButton.setEnabled(status);
        fourthOptionButton.setEnabled(status);

    }

    private void delay(int delay, String control) {
        new Handler().postDelayed(() -> {
            switch (control) {
                case "Collect":
                    count++;
                    countTextView.setText(String.valueOf(count));
                    firstOptionButton.setText("1");
                    secondOptionButton.setText("2");
                    thirdOptionButton.setText("3");
                    fourthOptionButton.setText("4");
                    setEnabledForOptionButtons(true);
                    if (optionList.get(EXMIC).isValue()) {
                        getSpeechInput("Base");

                    }
                    break;
                case "Wrong":
                    collectPopUp("Wrong");
                    break;

            }
        }, delay);

    }

    private void collectPopUp(String control) {
        speechRecognizer.destroy();
        dialogBuilder = new AlertDialog.Builder(this);
        View questionPopUpView = getLayoutInflater().inflate(R.layout.template_question_pop_up, null);
        xImageViewPopUp = questionPopUpView.findViewById(R.id.xImageViewPopUp);
        ImageView questionIcon = questionPopUpView.findViewById(R.id.templateImageViewPopUp);
        TextView infoTextViewPopUp = questionPopUpView.findViewById(R.id.templateInfoTextViewPopUp);
        collectButtonPopUp = questionPopUpView.findViewById(R.id.continueButtonPopUp);

        switch (control) {
            case "Collect":
                infoTextViewPopUp.setText(collectQuestionTextViewPopUpTextAudioString);
                collectButtonPopUp.setText(collectButtonPopUpTextString);
                checkOptions(collectQuestionTextViewPopUpTextAudioString, "PopUpCollect");
                break;
            case "Wrong":
                xImageViewPopUp.setVisibility(View.GONE);
                infoTextViewPopUp.setText(lostPrizeTextViewPopUpTextAudioString);
                collectButtonPopUp.setText(lostPrizeButtonPopUpTextString);
                checkOptions(lostPrizeTextViewPopUpTextAudioString, "PopUpWrong");
                break;

        }

        dialogBuilder.setView(questionPopUpView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        xImageViewPopUp.setOnClickListener((v) -> popUpExit());
        collectButtonPopUp.setOnClickListener((v) -> continueAction(control));

    }

    private void popUpExit() {
        speechRecognizer.destroy();
        dialog.dismiss();
        dialogBuilder = null;
        checkOptions(describeAudio, "Base");


    }

    private void continueAction(String control) {
        speechRecognizer.destroy();
        if (control.equals("Collect")) {
            updateData();

        }
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
        dialog.dismiss();
        finishAndRemoveTask();

    }

    private void updateData() {
        LoggedUserData.loggedSuperPowerCorrectAnswer = LoggedUserData.loggedSuperPowerCorrectAnswer + rightAnswerCount;
        LoggedUserData.loggedSuperPowerFiftyFifty = LoggedUserData.loggedSuperPowerFiftyFifty + fiftyFiftyCount;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

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
            checkOptions(describeAudio, "Base");
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
                        if (dialogBuilder == null) {
                            if (viewsEnableStatus) {
                                getSpeechInput("Base");

                            }

                        } else {
                            if (xImageViewPopUp.getVisibility() == View.VISIBLE) {
                                getSpeechInput("PopUpCollect");

                            } else {
                                getSpeechInput("PopUpWrong");

                            }

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
        if (textToSpeech == null) {
            Log.d("NULL", "NULL");

        }
        textToSpeech.speak(text, queueMode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

    }


    private void speechInitialize() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage);

    }

    private void getSpeechInput(String screen) {
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
                    getSpeechInput(screen);

                }
                if (error == SpeechRecognizer.ERROR_NETWORK) {
                    speechRecognizer.destroy();
                    if (connectionStatus) {
                        connectionStatus = false;
                        Toast.makeText(getApplicationContext(), lostConnectionToastAudio, Toast.LENGTH_SHORT).show();
                        if (optionList.get(EXSPEAKER).isValue()) {
                            speak(lostConnectionToastAudio, QUEUE_ADD);

                        }

                    }

                }

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                voiceInput = result.get(0);
                Log.d("Luck Activity voice input " + screen + ":", result.get(0));
                switch (LoggedUserData.language) {
                    case "english":
                        switch (screen) {
                            case "Base":
                                speechInputEn(voiceInput);
                                break;
                            case "PopUpCollect":
                                speechInputPopUpCollectEn(voiceInput);
                                break;
                            case "PopUpWrong":
                                speechInputPopUpWrongEn(voiceInput);
                                break;

                        }
                        break;
                    case "romanian":
                        switch (screen) {
                            case "Base":
                                speechInputRou(voiceInput);
                                break;
                            case "PopUpCollect":
                                speechInputPopUpCollectRou(voiceInput);
                                break;
                            case "PopUpWrong":
                                speechInputPopUpWrongRou(voiceInput);
                                break;

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

    private void invalidVoiceInput(String screen) {
        Toast.makeText(this, invalidCommandToastAudio, Toast.LENGTH_SHORT).show();
        checkOptions(invalidCommandToastAudio, screen);

    }

    private void checkOptions(String feedback, String screen) {
        if (optionList.get(EXSPEAKER).isValue()) {
            speak(feedback, QUEUE_ADD);

        } else {
            if (optionList.get(EXMIC).isValue() && viewsEnableStatus) {
                getSpeechInput(screen);

            }

        }

    }

    private void speechInputEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "1":
                firstOptionButton.performClick();
                break;
            case "2":
                secondOptionButton.performClick();
                break;
            case "3":
                thirdOptionButton.performClick();
                break;
            case "4":
                fourthOptionButton.performClick();
                break;
            case "collect":
                collectPopUp("Collect");
                break;
            case "prize":
                speechRecognizer.destroy();
                checkOptions(fiftyFiftyCount + SPACESTRING + fiftyFiftyNumberAudio + SPACESTRING + rightAnswerCount + SPACESTRING + rightAnswerNumberAudio, "base");
                break;
            default:
                invalidVoiceInput("Base");

        }

    }

    private void speechInputRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "1":
                firstOptionButton.performClick();
                break;
            case "2":
                secondOptionButton.performClick();
                break;
            case "3":
                thirdOptionButton.performClick();
                break;
            case "4":
                fourthOptionButton.performClick();
                break;
            case "colectează":
                collectPopUp("Collect");
                break;
            case "premiu":
                speechRecognizer.destroy();
                checkOptions(fiftyFiftyCount + SPACESTRING + fiftyFiftyNumberAudio + SPACESTRING + rightAnswerCount + SPACESTRING + rightAnswerNumberAudio, "base");
                break;
            default:
                invalidVoiceInput("Base");

        }

    }

    private void speechInputPopUpCollectEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "next":
                collectButtonPopUp.performClick();
                return;
            case "exit":
                xImageViewPopUp.performClick();
                return;
            default:
                invalidVoiceInput("PopUpCollect");

        }

    }

    private void speechInputPopUpCollectRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        switch (voiceInput) {
            case "continuă":
                collectButtonPopUp.performClick();
                return;
            case "închide":
                xImageViewPopUp.performClick();
                return;
            default:
                invalidVoiceInput("PopUpCollect");

        }

    }

    private void speechInputPopUpWrongEn(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        if ("close".equals(voiceInput)) {
            collectButtonPopUp.performClick();

        } else {
            invalidVoiceInput("PopUpWrong");

        }

    }

    private void speechInputPopUpWrongRou(String voiceInput) {
        voiceInput = voiceInput.toLowerCase();
        if ("închide".equals(voiceInput)) {
            collectButtonPopUp.performClick();

        } else {
            invalidVoiceInput("PopUpWrong");

        }

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && currentActivity instanceof GameActivity) {
                    Log.d("Luck", "connectionListener");
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
        if (dialogBuilder == null) {
            checkOptions(connectedToastAudio, "Base");
        } else {
            if (xImageViewPopUp.getVisibility() == View.VISIBLE) {
                checkOptions(connectedToastAudio, "PopUpCollect");

            } else {
                checkOptions(connectedToastAudio, "PopUpWrong");

            }

        }

    }

    private void lossConnection() {
        if (!optionList.get(EXMIC).isValue()) {
            connectionStatus = false;
            Toast.makeText(getApplicationContext(), lostConnectionToastAudio, Toast.LENGTH_SHORT).show();
            if (optionList.get(EXSPEAKER).isValue()) {
                speak(lostConnectionToastAudio, QUEUE_ADD);

            }

        }

    }

}
