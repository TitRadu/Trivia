package com.example.triviaapp.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.triviaapp.Answer;
import com.example.triviaapp.FirebaseCallback;
import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.Option;
import com.example.triviaapp.Question;
import com.example.triviaapp.R;
import com.example.triviaapp.game.ui.SubmitButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.example.triviaapp.FirebaseHelper.connectedRef;
import static com.example.triviaapp.LoggedUserData.EMPTYSTRING;
import static com.example.triviaapp.LoggedUserData.EXMIC;
import static com.example.triviaapp.LoggedUserData.EXSPEAKER;
import static com.example.triviaapp.LoggedUserData.MIC;
import static com.example.triviaapp.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.LoggedUserData.SPEAKER;
import static com.example.triviaapp.LoggedUserData.connectionStatus;
import static com.example.triviaapp.LoggedUserData.currentActivity;
import static com.example.triviaapp.LoggedUserData.dailyQuestion;
import static com.example.triviaapp.LoggedUserData.loggedSuperPowerCorrectAnswer;
import static com.example.triviaapp.LoggedUserData.loggedSuperPowerFiftyFifty;
import static com.example.triviaapp.LoggedUserData.optionList;

public class PlayActivity extends AppCompatActivity {

    public static final int TOTAL_QUESTION_TO_WIN_GAME = 11;
    String userAnswer, correctAnswer, voiceInput = null;
    Button nextQuestionButton, tryAgainButton, btn_superpower, btn_RightAnswer;
    SubmitButton btnA, btnB, btnC, btnD, selectedThroughVoiceOption;
    TextView question, questionCounterTextView, timerView, totalScoreView, questionScoreView, totalScoreNextView, questionScoreViewScore, totalScoreNextViewPoints;
    Switch microphoneSwitch;
    ProgressBar progressBar;
    MaterialCardView materialCardView;
    int questionCounter;
    boolean userAnswerIsCorrect;
    boolean answerWasSet;
    List<Question> questions;
    List<Answer> answers;
    Question currentQuestion;
    HashMap<String, Object> map = new HashMap<>();
    int totalPoints = 0;
    int time = 0;
    int progressBarPercent = 0;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    LinearLayout firstLineButtonsLayout, infoLayout;
    private TextToSpeech textToSpeech;

    String totalScoreTextViewString, questionTextViewString;
    String invalidInputToast;
    String youAnsweredTextAudio, timeExpiredTextAudio;
    String remainings, rights;
    Locale appLanguage;

    String speakerControl;

    List<Answer> currentAnswers;

    private boolean connectionListenerStatus = false;

    String gameMenuAudio, gameWonTextAudio, wonFFTextAudio, wonRATextAudio, correctAnswerTextAudio, wrongAnswerTextAudio, bonusRATextAudio, connectedToastAudio, connectionLostToastAudio, invalidCommandToastAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initialize();
        setViews();
        setListenerForMicrophoneSwitch();
        setBonusButtonsListener();

    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();

        }
        super.onDestroy();

    }

    private void setTextToSpeechListener() {
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            setConnectionListener();
            speak(obtainQuestionSpeech(), QUEUE_ADD);

        });

    }

    private void verifyTextToSpeechListenerStatus(int status) {
        if (status == TextToSpeech.SUCCESS) {
            setProgressListener();
            int result = textToSpeech.setLanguage(appLanguage);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(0.75f);


            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getBaseContext(), "Language not supported!", Toast.LENGTH_SHORT).show();

            }

        } else {
            Toast.makeText(getBaseContext(), "Initialization failed!", Toast.LENGTH_SHORT).show();

        }

    }

    private void setProgressListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                speechRecognizer.destroy();

            }

            @Override
            public void onDone(String utteranceId) {
                Runnable runnable = () -> {
                    if (optionList.get(SPEAKER).isValue() || optionList.get(EXSPEAKER).isValue()) {

                        if (speakerControl.equals("Nothing")) {
                            if (optionList.get(MIC).isValue() || optionList.get(EXMIC).isValue()) {
                                Log.d("MIC", "NOTHING");
                                getSpeechInput();
                            }

                        }
                        if (speakerControl.equals("Question")) {
                            timer();
                            if (optionList.get(MIC).isValue() || optionList.get(EXMIC).isValue()) {
                                Log.d("MIC", "QUESTION");
                                getSpeechInput();
                            }
                            speakerControl = "Nothing";

                        }
                        if (speakerControl.equals("Delay")) {
                            delay(3000);
                            speakerControl = "Nothing";

                        }

                    }

                };

                runOnUiThread(runnable);

            }

            @Override
            public void onError(String utteranceId) {

            }

        });

    }

    private void speak(String text, int queueMode) {
        textToSpeech.speak(text, queueMode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

    }

    private String obtainQuestionSpeech() {
        String text = currentQuestion.getQuestion();
        text = text + ". " + btnA.getText();
        text = text + ". " + btnB.getText();
        text = text + ". " + btnC.getText();
        text = text + ". " + btnD.getText();
        return text;
    }

    @SuppressLint("SetTextI18n")
    private void setBonusButtonsListener() {
        btn_superpower.setOnClickListener(v -> {
            speechRecognizer.destroy();
            LoggedUserData.loggedSuperPowerFiftyFifty--;
            setAnswersAfterFiftyFifty();
            btn_superpower.setEnabled(false);
            btn_superpower.setText("50 - 50 \n " + LoggedUserData.loggedSuperPowerFiftyFifty + " " + remainings);
        });
        btn_RightAnswer.setOnClickListener(v -> {
            speechRecognizer.destroy();
            LoggedUserData.loggedSuperPowerCorrectAnswer--;
            clickCorrectAnswer();
            btn_RightAnswer.setEnabled(false);
            btn_RightAnswer.setText(rights + LoggedUserData.loggedSuperPowerCorrectAnswer + " " + remainings);
        });
    }

    private void clickCorrectAnswer() {
        switch (correctAnswer) {
            case "A":
                clicked(btnA);
                break;
            case "B":
                clicked(btnB);
                break;
            case "C":
                clicked(btnC);
                break;
            case "D":
                clicked(btnD);
                break;
        }
    }

    private void setViewsProprieties() {
        setVisibilityForViews();
        setEnabledForAnswersButtons(false);
        setVisibilityForBonusButtons();
        setEnabledFalseForBonusButtons();

    }

    private void setVisibilityForViews() {
        if (LoggedUserData.dailyQuestion) {
            questionCounterTextView.setVisibility(View.INVISIBLE);
            totalScoreView.setVisibility(View.INVISIBLE);

        }

    }

    public void setViews() {
        btn_RightAnswer = findViewById(R.id.btn_superPowerRightAnswer);
        btn_superpower = findViewById(R.id.btn_superPower);
        questionScoreViewScore = findViewById(R.id.questionScoreViewPoints);
        totalScoreNextViewPoints = findViewById(R.id.totalScoreNextViewPoints);
        userAnswer = EMPTYSTRING;
        btnA = findViewById(R.id.varA);
        btnB = findViewById(R.id.varB);
        btnC = findViewById(R.id.varC);
        btnD = findViewById(R.id.varD);
        question = findViewById(R.id.question);
        questionCounterTextView = findViewById(R.id.questionCounter);
        timerView = findViewById(R.id.timerView);
        totalScoreView = findViewById(R.id.totalScoreView);
        questionCounter = 1;
        answerWasSet = false;
        selectedThroughVoiceOption = null;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        firstLineButtonsLayout = findViewById(R.id.firstLineButtonsLayout);
        infoLayout = findViewById(R.id.infoLayout);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        tryAgainButton = findViewById(R.id.tryAgainButton);
        questionScoreView = findViewById(R.id.questionScoreView);
        totalScoreNextView = findViewById(R.id.totalScoreNextView);
        progressBar = findViewById(R.id.progress_bar);
        materialCardView = findViewById(R.id.materialCardView);
        microphoneSwitch = findViewById(R.id.sw_microphonePlay);
        microphoneSwitch.setChecked(optionList.get(MIC).isValue());
        speakerControl = "Nothing";
        setViewsProprieties();
        chooseLanguage();
        setTextViewWithQuestionAndAnswers();

    }

    private void setViewForEnglishLanguage() {
        microphoneSwitch.setText(R.string.microphoneSwitchLogMenuEditPlayEn);
        totalScoreTextViewString = getString(R.string.totalScoreTextViewPlayEn);
        questionTextViewString = getString(R.string.questionTextViewPlayEn);
        nextQuestionButton.setText(R.string.nextButtonLogMenuPlayEn);
        tryAgainButton.setText(R.string.tryAgainButtonPlayEn);
        questionScoreView.setText(R.string.questionScoreTextViewPlayEn);
        totalScoreNextView.setText(R.string.totalScoreNextTextViewPlayEn);
        invalidInputToast = getString(R.string.invalidInputToastPlayEn);
        youAnsweredTextAudio = getString(R.string.youAnsweredTextAudioPlayEn);
        timeExpiredTextAudio = getString(R.string.timeExpiredTextPlayEn);
        appLanguage = Locale.ENGLISH;
        remainings = getString(R.string.superPowerRemainingEn);
        rights = getString(R.string.superPowerRightAnwerEn);
        gameMenuAudio = getString(R.string.gameMenuAudioPlayEn);
        gameWonTextAudio = getString(R.string.gameWonAudioPlayEn);
        wonFFTextAudio = getString(R.string.wonFFAudioPlayLuckEn);
        wonRATextAudio = getString(R.string.wonRAAudioPlayLuckEn);
        correctAnswerTextAudio = getString(R.string.correctAnswerAudioPlayEn);
        wrongAnswerTextAudio = getString(R.string.wrongAnswerAudioPlayLuckEn);
        bonusRATextAudio = getString(R.string.bonusRAAudioPlayEn);
        connectedToastAudio = getString(R.string.connectionToastAudioEn);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioEn);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioEn);

    }

    private void setViewForRomanianLanguage() {
        microphoneSwitch.setText(R.string.microphoneSwitchLogMenuEditPlayRou);
        totalScoreTextViewString = getString(R.string.totalScoreTextViewPlayRou);
        questionTextViewString = getString(R.string.questionTextViewPlayRou);
        nextQuestionButton.setText(R.string.nextButtonLogMenuPlayRou);
        tryAgainButton.setText(R.string.tryAgainButtonPlayRou);
        questionScoreView.setText(R.string.questionScoreTextViewPlayRou);
        totalScoreNextView.setText(R.string.totalScoreNextTextViewPlayRou);
        invalidInputToast = getString(R.string.invalidInputToastPlayRou);
        youAnsweredTextAudio = getString(R.string.youAnsweredTextAudioPlayRou);
        timeExpiredTextAudio = getString(R.string.timeExpiredTextPlayRou);
        appLanguage = Locale.getDefault();
        remainings = getString(R.string.superPowerRemainingRou);
        rights = getString(R.string.superPowerRightAnwerRou);
        gameMenuAudio = getString(R.string.gameMenuAudioPlayRou);
        gameWonTextAudio = getString(R.string.gameWonAudioPlayRou);
        wonFFTextAudio = getString(R.string.wonFFAudioPlayLuckRou);
        wonRATextAudio = getString(R.string.wonRAAudioPlayLuckRou);
        correctAnswerTextAudio = getString(R.string.correctAnswerAudioPlayRou);
        wrongAnswerTextAudio = getString(R.string.wrongAnswerAudioPlayLuckRou);
        bonusRATextAudio = getString(R.string.bonusRAAudioPlayRou);
        connectedToastAudio = getString(R.string.connectionToastAudioRou);
        connectionLostToastAudio = getString(R.string.connectionLostToastAudioRou);
        invalidCommandToastAudio = getString(R.string.invalidCommandToastAudioRou);
    }

    private void setTextForViewsWithComplexText() {
        totalScoreView.setText(totalScoreTextViewString + "   " + totalPoints);
        questionCounterTextView.setText(questionTextViewString + "   " + questionCounter + " / 10");
        btn_superpower.setText("50 - 50 \n " + LoggedUserData.loggedSuperPowerFiftyFifty + " " + remainings);
        btn_RightAnswer.setText(rights + LoggedUserData.loggedSuperPowerCorrectAnswer + " " + remainings);
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
        setTextForViewsWithComplexText();

    }

    private void setVisibilityForBonusButtons() {
        if (LoggedUserData.dailyQuestion) {
            btn_superpower.setVisibility(View.GONE);
            btn_RightAnswer.setVisibility(View.GONE);

        }

    }

    private void setEnabledForBonusButtons() {
        if (LoggedUserData.loggedSuperPowerFiftyFifty > 0) {
            btn_superpower.setEnabled(true);

        } else {
            btn_superpower.setEnabled(false);
            btn_superpower.setTextColor(Color.GRAY);
        }
        if (LoggedUserData.loggedSuperPowerCorrectAnswer > 0) {
            btn_RightAnswer.setEnabled(true);

        } else {
            btn_RightAnswer.setEnabled(false);
            btn_RightAnswer.setTextColor(Color.GRAY);
        }
    }

    private void updateProgressBar() {
        progressBar.setProgress(progressBarPercent);
    }

    private void initialize() {
        currentActivity = this;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//deschide o activitate ce solicita utilizatorului sa vorbeasca si trimite mesajul catre un SpeechRecognizer.
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, appLanguage);

    }

    private String getLanguage(String param) {
        if (LoggedUserData.language.equals("english")) {
            return param.concat("En");
        } else {
            return param;
        }
    }

    private void readQuestionData(final FirebaseCallback firebaseCallback) {
        FirebaseHelper.questionDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nodeQuestion = getLanguage("question");
                if (questions.isEmpty()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Question question = new Question(Integer.parseInt(dataSnapshot1.getKey()),
                                dataSnapshot1.child(nodeQuestion).getValue(String.class),
                                dataSnapshot1.child("category").getValue(String.class));

                        for (Option o : optionList)
                            if (o.isValue() && o.getName().equals(question.getCategory()) || LoggedUserData.dailyQuestion) {
                                questions.add(question);
                                break;

                            }

                    }
                    firebaseCallback.onCallbackQuestions(questions);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void readAnswersData(final FirebaseCallback firebaseCallback) {
        FirebaseHelper.answerDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String answerNode = getLanguage("answer");
                if (answers.isEmpty()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Answer a = new Answer
                                (Integer.parseInt(dataSnapshot1.getKey()),
                                        dataSnapshot.child(String.valueOf(dataSnapshot1.getKey())).child(answerNode).getValue(String.class),
                                        dataSnapshot.child(String.valueOf(dataSnapshot1.getKey())).child("correct").getValue(Boolean.class),
                                        dataSnapshot.child(String.valueOf(dataSnapshot1.getKey())).child("questionId").getValue(Integer.class)
                                );
                        answers.add(a);

                    }
                    firebaseCallback.onCallbackAnswers(answers);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    public void setTextViewWithQuestionAndAnswers() {
        readQuestionData(new FirebaseCallback() {
            @Override
            public void onCallbackQuestions(List<Question> questions) {
                setQuestion(questions);

            }

            @Override
            public void onCallbackAnswers(List<Answer> answers) {

            }

        });
        readAnswersData(new FirebaseCallback() {
            @Override
            public void onCallbackQuestions(List<Question> questions) {

            }

            @Override
            public void onCallbackAnswers(List<Answer> answers) {
                setAnswers(answers);
                if (optionList.get(SPEAKER).isValue() || optionList.get(EXSPEAKER).isValue()) {
                    speakerControl = "Question";
                    setTextToSpeechListener();

                }else{
                    if(optionList.get(MIC).isValue() || optionList.get(EXMIC).isValue()) {
                        getSpeechInput();

                    }
                 timer();

                }

            }

        });

    }

    private void setQuestion(List<Question> questions) {
        Random rand = new Random();

        if (questions.size() != 0) {
            int random = rand.nextInt(questions.size());
            currentQuestion = questions.get(random);
            question.setText(currentQuestion.getQuestion());
            questions.remove(random);

        }

    }

    private void setAnswers(List<Answer> answers) {
        setButtonsVisible();
        currentAnswers = new ArrayList<>();
        Random rand = new Random();
        List<Integer> randomCurrentAnswersList = new ArrayList<>();
        int r;

        while (randomCurrentAnswersList.size() < 4) {
            r = rand.nextInt(4);
            if (!randomCurrentAnswersList.contains(r)) {
                randomCurrentAnswersList.add(r);

            }

        }

        if (answers.size() % 4 == 0 && answers.size() != 0) {
            for (Answer answer : answers) {
                if (answer.getQuestionId() == currentQuestion.getQuestionID()) {
                    currentAnswers.add(answer);

                }

            }

            btnA.setText("A: " + currentAnswers.get(randomCurrentAnswersList.get(0)).getAnswer());
            btnB.setText("B: " + currentAnswers.get(randomCurrentAnswersList.get(1)).getAnswer());
            btnC.setText("C: " + currentAnswers.get(randomCurrentAnswersList.get(2)).getAnswer());
            btnD.setText("D: " + currentAnswers.get(randomCurrentAnswersList.get(3)).getAnswer());


        }

        setCorrectAnswer(
                currentAnswers.get(randomCurrentAnswersList.get(0)).isCorrect(),
                currentAnswers.get(randomCurrentAnswersList.get(1)).isCorrect(),
                currentAnswers.get(randomCurrentAnswersList.get(2)).isCorrect(),
                currentAnswers.get(randomCurrentAnswersList.get(3)).isCorrect()
        );

    }

    private void setAnswersAfterFiftyFifty() {
        List<Integer> randomEliminateList = new ArrayList<>();

        int correct = -1;
        switch (correctAnswer) {
            case "A":
                correct = 0;
                break;
            case "B":
                correct = 1;
                break;
            case "C":
                correct = 2;
                break;
            case "D":
                correct = 3;
                break;

        }
        generateRandomAnswerForEliminate(randomEliminateList, correct);
        set2WrongAnswerAsInvisible(randomEliminateList);
        remainingAnswersFeedback(randomEliminateList);

    }

    private void set2WrongAnswerAsInvisible(List<Integer> randomEliminateList) {
        for (Integer i : randomEliminateList) {
            switch (i) {
                case 0:
                    btnA.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    btnB.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    btnC.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    btnD.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }

    private void generateRandomAnswerForEliminate(List<Integer> randomEliminateList, int correct) {
        Random rand = new Random();
        int r;
        while (randomEliminateList.size() != 2) {
            r = rand.nextInt(4);
            if (r != correct && !randomEliminateList.contains(r)) {
                randomEliminateList.add(r);
            }
        }
    }

    private void remainingAnswersFeedback(List<Integer> randomEliminateList) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i <= 3; i++) {
            if (!randomEliminateList.contains(i)) {
                switch (i) {
                    case 0:
                        if (text.length() == 0) {
                            text.append(btnA.getText());
                        } else {
                            text.append(". ").append(btnA.getText());

                        }
                        break;
                    case 1:
                        if (text.length() == 0) {
                            text.append(btnB.getText());
                        } else {
                            text.append(". ").append(btnB.getText());

                        }
                        break;
                    case 2:
                        if (text.length() == 0) {
                            text.append(btnC.getText());
                        } else {
                            text.append(". ").append(btnC.getText());

                        }
                        break;
                    case 3:
                        if (text.length() == 0) {
                            text.append(btnD.getText());
                        } else {
                            text.append(". ").append(btnD.getText());

                        }
                        break;
                }

            }

        }
        checkOptions(text.toString());

    }

    private void setButtonsVisible() {
        btnA.setVisibility(View.VISIBLE);
        btnB.setVisibility(View.VISIBLE);
        btnC.setVisibility(View.VISIBLE);
        btnD.setVisibility(View.VISIBLE);
    }


    void setCorrectAnswer(boolean first, boolean second, boolean third, boolean fourth) {
        if (first) {
            correctAnswer = "A";

        }
        if (second) {
            correctAnswer = "B";

        }
        if (third) {
            correctAnswer = "C";

        }
        if (fourth) {
            correctAnswer = "D";

        }

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
                if (nextQuestionButton.getVisibility() == View.GONE) {

                    switch (LoggedUserData.language) {
                        case "english":
                            afterQuestionSpeechInputEn(voiceInput);
                            break;
                        case "romanian":
                            afterQuestionSpeechInputRou(voiceInput);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
                    }

                } else {
                    switch (LoggedUserData.language) {
                        case "english":
                            afterNextSpeechInputEn(voiceInput);
                            break;
                        case "romanian":
                            afterNextSpeechInputRou(voiceInput);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
                    }

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

    private void afterQuestionSpeechInputEn(String voiceInput) {
        switch (voiceInput) {
            case "A":
            case "a":
                selectedThroughVoiceOption = btnA;
                break;
            case "B":
            case "b":
                selectedThroughVoiceOption = btnB;
                break;
            case "C":
            case "c":
                selectedThroughVoiceOption = btnC;
                break;
            case "D":
            case "d":
                selectedThroughVoiceOption = btnD;
                break;
            case "Fifty fifty":
            case "fifty fifty":
                if (!dailyQuestion) {
                    if (loggedSuperPowerFiftyFifty > 0) {
                        btn_superpower.performClick();

                    }

                } else {
                    invalidVoiceInput();

                }
                return;
            case "Right answer":
            case "right answer":
            case "right Answer":
                if (!dailyQuestion) {
                    if (loggedSuperPowerCorrectAnswer > 0) {
                        btn_RightAnswer.performClick();

                    }

                } else {
                    invalidVoiceInput();

                }
                return;
            default:
                speechRecognizer.destroy();
                invalidVoiceInput();
                return;

        }
        if (selectedThroughVoiceOption.getVisibility() == View.VISIBLE) {
            clicked(selectedThroughVoiceOption);
        } else {
            invalidVoiceInput();

        }

    }


    private void afterQuestionSpeechInputRou(String voiceInput) {
        switch (voiceInput) {
            case "A":
            case "a":
                selectedThroughVoiceOption = btnA;
                break;
            case "B":
            case "b":
                selectedThroughVoiceOption = btnB;
                break;
            case "C":
            case "c":
                selectedThroughVoiceOption = btnC;
                break;
            case "D":
            case "d":
                selectedThroughVoiceOption = btnD;
                break;
            case "50 50":
                if (!dailyQuestion) {
                    if (loggedSuperPowerFiftyFifty > 0) {
                        btn_superpower.performClick();

                    }

                } else {
                    invalidVoiceInput();

                }
                return;
            case "Răspuns corect":
            case "răspuns corect":
                if (!dailyQuestion) {
                    if (loggedSuperPowerCorrectAnswer > 0) {
                        btn_RightAnswer.performClick();

                    }

                } else {
                    invalidVoiceInput();

                }
                return;
            default:
                speechRecognizer.destroy();
                invalidVoiceInput();
                return;

        }
        if (selectedThroughVoiceOption.getVisibility() == View.VISIBLE) {
            clicked(selectedThroughVoiceOption);
        } else {
            invalidVoiceInput();

        }

    }

    private void afterNextSpeechInputEn(String voiceInput) {
        switch (voiceInput) {
            case "Next":
            case "next":
                nextQuestionButton.callOnClick();
                break;
            case "Try again":
            case "try again":
                if (tryAgainButton.getVisibility() == View.VISIBLE) {
                    tryAgainButton.callOnClick();

                } else {
                    invalidVoiceInput();

                }
                break;
            case "Score":
            case "score":
                if (dailyQuestion) {
                    invalidVoiceInput();
                    return;

                }
                String scoreText;
                scoreText = questionScoreView.getText().toString() + questionScoreViewScore.getText().toString();
                scoreText = scoreText + ". " + totalScoreNextView.getText().toString() + totalPoints;

                checkOptions(scoreText);
                break;
            case "Prize":
            case "prize":
                if (!dailyQuestion) {
                    invalidVoiceInput();
                    return;
                }
                String prizeText;
                prizeText = questionScoreView.getText().toString();

                checkOptions(prizeText);
                break;
            default:
                speechRecognizer.destroy();
                invalidVoiceInput();

        }

    }

    private void afterNextSpeechInputRou(String voiceInput) {
        switch (voiceInput) {
            case "Continuă":
            case "continuă":
                nextQuestionButton.callOnClick();
                break;
            case "Încearcă din nou":
            case "încearcă din nou":
                if (tryAgainButton.getVisibility() == View.VISIBLE) {
                    tryAgainButton.callOnClick();

                } else {
                    invalidVoiceInput();

                }
                break;
            case "Scor":
            case "scor":
                if (dailyQuestion) {
                    invalidVoiceInput();
                    return;

                }
                String scoreText;
                scoreText = questionScoreView.getText().toString() + questionScoreViewScore.getText().toString();
                if(questionCounter == TOTAL_QUESTION_TO_WIN_GAME) {
                    scoreText = scoreText + ". " + totalScoreNextView.getText().toString() + totalPoints + " X 2";

                }else{
                    scoreText = scoreText + ". " + totalScoreNextView.getText().toString() + totalPoints;

                }

                checkOptions(scoreText);
                break;
            case "Premiu":
            case "premiu":
                if (!dailyQuestion) {
                    invalidVoiceInput();
                    return;
                }
                String prizeText;
                prizeText = questionScoreView.getText().toString();

                checkOptions(prizeText);
                break;
            default:
                speechRecognizer.destroy();
                invalidVoiceInput();

        }

    }


    public void clicked(View view) {
        setEnabledForAnswersButtons(false);
        setEnabledFalseForBonusButtons();
        speechRecognizer.destroy();
        answerWasSet = true;

        ((SubmitButton) view).startAnimation();
        getUserAnswer(view);
        if (userAnswer.equals(correctAnswer)) {
            setExtraTimeForMicrophone();
            calculatePoints();
            userAnswerIsCorrect = true;
            questionCounter++;
            if (questionCounter == TOTAL_QUESTION_TO_WIN_GAME) {
                LoggedUserData.loggedUserPoints = LoggedUserData.loggedUserPoints + totalPoints * 2;
                ++LoggedUserData.loggedSuperPowerFiftyFifty;
                ++LoggedUserData.loggedGamesWon;
                increaseRightAnswerSuperpower();
                sendToDatabase();
            }
        } else {
            ((SubmitButton) view).setBtn_LineColor(Color.RED);
            ((SubmitButton) view).setAnimationColor(Color.RED);
            LoggedUserData.loggedUserPoints = LoggedUserData.loggedUserPoints + totalPoints;
            sendToDatabase();
            userAnswerIsCorrect = false;

        }
        if (!optionList.get(EXSPEAKER).isValue()) {
            delay(3000);

        } else {
            if (optionList.get(EXSPEAKER).isValue()) {
                speakerControl = "Delay";
                speak(youAnsweredTextAudio, QUEUE_ADD);

            }

        }

    }

    private void increaseRightAnswerSuperpower() {
        if (LoggedUserData.loggedGamesWon % 2 == 0) {
            ++LoggedUserData.loggedSuperPowerCorrectAnswer;
        }
    }

    private void calculatePoints() {
        String timer = timerView.getText().toString().replaceFirst(" ", "");
        int d = Integer.parseInt(timer);
        time = time + d;
        totalPoints = totalPoints + time;
        totalScoreView.setText(totalScoreTextViewString + "   " + totalPoints);

    }

    private void setExtraTimeForMicrophone() {
        if (voiceInput == null) {
            time = 0;

        } else {
            time = 2;
        }
    }

    private void getUserAnswer(View view) {
        switch (view.getId()) {
            case R.id.varA:
                userAnswer = "A";
                break;
            case R.id.varB:
                userAnswer = "B";
                break;
            case R.id.varC:
                userAnswer = "C";
                break;
            case R.id.varD:
                userAnswer = "D";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void sendToDatabase() {
        populateMapWithUserData();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

    }

    private void populateMapWithUserData() {
        map = new HashMap<>();
        map.put("email", LoggedUserData.loggedUserEmail);
        map.put("gamesWon", LoggedUserData.loggedGamesWon);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower", LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("superpowerCorrectAnswer", LoggedUserData.loggedSuperPowerCorrectAnswer);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("dailyQuestionTime", LoggedUserData.loggedUserDailyQuestionTime);
        map.put("luckModeTime", LoggedUserData.loggedUserLuckModeTime);
    }

    private void generateRandomPrizeForDailyQuestion() {
        Random rand = new Random();
        int r = rand.nextInt(2);

        if (userAnswerIsCorrect) {
            if (r == 0) {
                questionScoreView.setText(wonFFTextAudio);
                speechRecognizer.destroy();
                checkOptions(wonFFTextAudio);
                loggedSuperPowerFiftyFifty++;

            } else {
                questionScoreView.setText(wonRATextAudio);
                speechRecognizer.destroy();
                checkOptions(wonRATextAudio);
                loggedSuperPowerCorrectAnswer++;

            }
            sendToDatabase();
        } else {
            questionScoreView.setText(wrongAnswerTextAudio);
            speechRecognizer.destroy();
            checkOptions(wrongAnswerTextAudio);

        }

    }

    private void delay(int delay) {
        new Handler().postDelayed(() -> {
            progressBarPercent = 0;
            progressBar.setProgress(progressBarPercent);
            if (LoggedUserData.dailyQuestion) {
                generateRandomPrizeForDailyQuestion();
            }
            updateUIAfterAnswerToTheQuestion();
            setTextAfterAnswerToTheQuestion();
            if (!dailyQuestion) {
                String text;
                if(userAnswerIsCorrect){
                    if(questionCounter == TOTAL_QUESTION_TO_WIN_GAME){
                        text = gameWonTextAudio;
                        if (LoggedUserData.loggedGamesWon % 2 == 0) {
                            text = text + SPACESTRING + bonusRATextAudio;
                        }

                    }else {

                        text = correctAnswerTextAudio;
                    }

                }
                else{
                    text = wrongAnswerTextAudio;

                }

                if (optionList.get(EXSPEAKER).isValue()) {
                    speak(text, QUEUE_ADD);

                } else {
                    if (optionList.get(MIC).isValue() || optionList.get(EXMIC).isValue()) {
                        getSpeechInput();

                    }

                }

            }
        }, delay);

    }

    private void timer() {
        setEnabledForAnswersButtons(true);
        if (!dailyQuestion) {
            setEnabledForBonusButtons();
        }
        final long prev[] = new long[1];
        prev[0] = 0;
        new CountDownTimer(30000, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (answerWasSet) {
                    question.setText(youAnsweredTextAudio);
                    cancel();
                    return;

                }
                if (millisUntilFinished / 1000 % 3 == 0 && millisUntilFinished / 1000 != prev[0]) {
                    prev[0] = millisUntilFinished / 1000;
                    progressBarPercent += 10;
                    updateProgressBar();
                }
                if (millisUntilFinished >= 10000) {
                    timerView.setText(String.valueOf(millisUntilFinished / 1000));

                } else {
                    timerView.setText(SPACESTRING + millisUntilFinished / 1000);

                }

            }

            @Override
            public void onFinish() {
                question.setText(timeExpiredTextAudio);
                speechRecognizer.destroy();
                checkOptions(timeExpiredTextAudio + gameMenuAudio);
                LoggedUserData.loggedUserPoints = LoggedUserData.loggedUserPoints + totalPoints;
                sendToDatabase();
                userAnswerIsCorrect = false;
                setTextAfterAnswerToTheQuestion();
                updateUIAfterAnswerToTheQuestion();

            }

        }.start();

    }

    private boolean isGameFinished() {
        if ((questionCounter == 11 || !userAnswerIsCorrect) || (questionCounter == 2 && LoggedUserData.dailyQuestion)) {
            LoggedUserData.dailyQuestion = false;
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            finishAndRemoveTask();
            return true;
        }
        return false;

    }

    private void updateUIAfterAnswerToTheQuestion() {
        infoLayout.setVisibility(View.GONE);
        materialCardView.setVisibility(View.GONE);
        firstLineButtonsLayout.setVisibility(View.GONE);
        nextQuestionButton.setVisibility(View.VISIBLE);
        if ((questionCounter == TOTAL_QUESTION_TO_WIN_GAME || !userAnswerIsCorrect) && !LoggedUserData.dailyQuestion) {
            tryAgainButton.setVisibility(View.VISIBLE);

        }
        microphoneSwitch.setVisibility(View.VISIBLE);
        questionScoreView.setVisibility(View.VISIBLE);

        if (!LoggedUserData.dailyQuestion) {
            btn_superpower.setVisibility(View.GONE);
            btn_RightAnswer.setVisibility(View.GONE);
            questionScoreViewScore.setVisibility(View.VISIBLE);
            totalScoreNextView.setVisibility(View.VISIBLE);
            totalScoreNextViewPoints.setVisibility(View.VISIBLE);

        }

    }

    private void setTextAfterAnswerToTheQuestion() {
        questionScoreViewScore.setText(String.valueOf(time));
        if (questionCounter == TOTAL_QUESTION_TO_WIN_GAME) {
            totalScoreNextViewPoints.setText(totalPoints + " X 2");

        } else {
            totalScoreNextViewPoints.setText(String.valueOf(totalPoints));

        }

    }

    private void setListenerForMicrophoneSwitch() {
        microphoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            optionList.get(MIC).setValue(isChecked);
            SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            if (!isChecked) {
                speechRecognizer.destroy();
            } else {
                if (speechIntent == null) {
                    initialize();
                }
                getSpeechInput();
            }
            editor.putString("mic", String.valueOf(isChecked));
            editor.apply();
        });
    }

    private void updateUIForTheNextQuestion() {
        microphoneSwitch.setVisibility(View.GONE);
        nextQuestionButton.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.GONE);
        questionScoreView.setVisibility(View.GONE);
        totalScoreNextView.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
        timerView.setText("30");
        timerView.setVisibility(View.VISIBLE);
        materialCardView.setVisibility(View.VISIBLE);
        firstLineButtonsLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        questionScoreViewScore.setVisibility(View.GONE);
        totalScoreNextViewPoints.setVisibility(View.GONE);
        if (!dailyQuestion) {
            btn_superpower.setVisibility(View.VISIBLE);
            btn_RightAnswer.setVisibility(View.VISIBLE);

        }

    }

    public void nextQuestionSetup(View view) {
        speechRecognizer.destroy();

        if (isGameFinished()) {
            return;

        }
        updateUIForTheNextQuestion();
        btnA.resetButton();
        btnB.resetButton();
        btnC.resetButton();
        btnD.resetButton();
        time = 0;
        questionCounterTextView.setText(questionTextViewString + "   " + questionCounter + " / 10");
        setQuestion(questions);
        setAnswers(answers);
        answerWasSet = false;
        voiceInput = null;
        selectedThroughVoiceOption = null;
        if (!optionList.get(SPEAKER).isValue() && !optionList.get(EXSPEAKER).isValue()) {
            if (optionList.get(MIC).isValue() || optionList.get(EXMIC).isValue()) {
                getSpeechInput();
            }
            timer();

        } else {
            speakerControl = "Question";
            speak(obtainQuestionSpeech(), QUEUE_FLUSH);

        }

    }

    public void tryAgain(View view) {
        Intent intent = new Intent(this, GameSettingsActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    private void setEnabledForAnswersButtons(boolean status) {
        btnA.setEnabled(status);
        btnB.setEnabled(status);
        btnC.setEnabled(status);
        btnD.setEnabled(status);

    }

    private void setEnabledFalseForBonusButtons() {
        btn_superpower.setEnabled(false);
        btn_RightAnswer.setEnabled(false);

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

    private void invalidVoiceInput() {
        Toast.makeText(this,invalidCommandToastAudio, Toast.LENGTH_SHORT).show();
        checkOptions(invalidCommandToastAudio);

    }

    private void setConnectionListener() {
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (connectionListenerStatus && currentActivity instanceof PlayActivity && textToSpeech != null) {
                    Log.d("Play","connectionListener");
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

