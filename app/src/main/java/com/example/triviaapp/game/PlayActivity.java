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
import static com.example.triviaapp.LoggedUserData.EMPTYSTRING;
import static com.example.triviaapp.LoggedUserData.MIC;
import static com.example.triviaapp.LoggedUserData.SPACESTRING;
import static com.example.triviaapp.LoggedUserData.SPEAKER;
import static com.example.triviaapp.LoggedUserData.dailyQuestion;
import static com.example.triviaapp.LoggedUserData.loggedSuperPowerCorrectAnswer;
import static com.example.triviaapp.LoggedUserData.loggedSuperPowerFiftyFifty;
import static com.example.triviaapp.LoggedUserData.optionList;

public class PlayActivity extends AppCompatActivity {

    public static final int TOTAL_QUESTION_TO_WIN_GAME = 11;
    String userAnswer, correctAnswer, voiceInput = null;
    Button  nextQuestionButton, tryAgainButton, btn_superpower, btn_RightAnswer;
    SubmitButton btnA,btnB, btnC, btnD, selectedThroughVoiceOption;
    TextView question, questionCounterTextView, timerView, totalScoreView, questionScoreView, totalScoreNextView,questionScoreViewScore,totalScoreNextViewPoints;
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
    int progressBarPercent=0;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    LinearLayout firstLineButtonsLayout, infoLayout;
    private TextToSpeech textToSpeech;

    String totalScoreTextViewString, questionTextViewString;
    String invalidInputToast;
    String youAnsweredText, timeExpiredText;
    String remainings,rights;
    Locale appLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setViews();
        if(!optionList.get(SPEAKER).isValue()){
            if(optionList.get(MIC).isValue()) {
                getSpeechInput();
            }
            timer();
        }
        setListenerForMicrophoneSwitch();
        setBonusButtonsListener();
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();

        }
        super.onDestroy();

    }

    private void setTextToSpeechListener(){
        textToSpeech = new TextToSpeech(this, status -> {
            verifyTextToSpeechListenerStatus(status);
            speak(obtainQuestionSpeech(),QUEUE_ADD);

        });

    }

    private void verifyTextToSpeechListenerStatus(int status) {
        if(status == TextToSpeech.SUCCESS){
            setProgressListener();
            int result = textToSpeech.setLanguage(appLanguage);
            textToSpeech.setPitch(1);
            textToSpeech.setSpeechRate(0.75f);


            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
               Toast.makeText(getBaseContext(), "Language not supported!",Toast.LENGTH_SHORT).show();

            }

        }else{
            Toast.makeText(getBaseContext(), "Initialization failed!",Toast.LENGTH_SHORT).show();

        }
    }

    private void setProgressListener(){
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                Runnable runnable = () -> {
                    if(optionList.get(SPEAKER).isValue()){
                        if(optionList.get(MIC).isValue()) {
                            getSpeechInput();
                        }
                        timer();
                    }
                };

                runOnUiThread(runnable);

            }

            @Override
            public void onError(String utteranceId) {

            }
        });

    }

    private void speak(String text, int queueMode){
        textToSpeech.speak(text,queueMode,null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);

    }

    private String obtainQuestionSpeech() {
        String text = currentQuestion.getQuestion();
        text = text + "\n" + btnA.getText();
        text = text + "\n" + btnB.getText();
        text = text + "\n" + btnC.getText();
        text = text + "\n" + btnD.getText();
        return text;
    }

    @SuppressLint("SetTextI18n")
    private void setBonusButtonsListener(){
        btn_superpower.setOnClickListener(v -> {
            LoggedUserData.loggedSuperPowerFiftyFifty--;
            setAnswers(answers,true);
            btn_superpower.setEnabled(false);
            btn_superpower.setText("50 - 50 \n "+LoggedUserData.loggedSuperPowerFiftyFifty + " " + remainings);
        });
        btn_RightAnswer.setOnClickListener(v -> {
            LoggedUserData.loggedSuperPowerCorrectAnswer--;
            clickCorrectAnswer();
            btn_RightAnswer.setEnabled(false);
            btn_RightAnswer.setText(rights+LoggedUserData.loggedSuperPowerCorrectAnswer + " " + remainings);
        });
    }

    private void clickCorrectAnswer() {
        switch (correctAnswer){
            case "A":clicked(btnA);break;
            case "B":clicked(btnB);break;
            case "C":clicked(btnC);break;
            case "D":clicked(btnD);break;
        }
    }

    private void setButtonsProprieties() {
        setEnabledForAnswersButtons(false);
        setVisibilityForBonusButtons();
        if(!dailyQuestion) {
            setEnabledFalseForBonusButtons();

        }
    }

    public void setViews(){
        btn_RightAnswer = findViewById(R.id.btn_superPowerRightAnswer);
        btn_superpower = findViewById(R.id.btn_superPower);
        questionScoreViewScore = findViewById(R.id.questionScoreViewPoints);
        totalScoreNextViewPoints = findViewById(R.id.totalScoreNextViewPoints);
        userAnswer = EMPTYSTRING;
        btnA = findViewById(R.id.varA);
        btnB = findViewById(R.id.varB);
        btnC = findViewById(R.id.varC);
        btnD = findViewById(R.id.varD);
        setButtonsProprieties();
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
        if(LoggedUserData.dailyQuestion){
            questionCounterTextView.setVisibility(View.INVISIBLE);
            totalScoreView.setVisibility(View.INVISIBLE);

        }
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        tryAgainButton = findViewById(R.id.tryAgainButton);
        questionScoreView = findViewById(R.id.questionScoreView);
        totalScoreNextView = findViewById(R.id.totalScoreNextView);
        progressBar = findViewById(R.id.progress_bar);
        materialCardView = findViewById(R.id.materialCardView);
        microphoneSwitch = findViewById(R.id.sw_microphonePlay);
        microphoneSwitch.setChecked(optionList.get(MIC).isValue());
        chooseLanguage();
        if(optionList.get(MIC).isValue()) {
            speechInitialize();
        }

        setTextViewWithQuestionAndAnswers(false);

    }

    private void setViewForEnglishLanguage(){
        microphoneSwitch.setText(R.string.microphoneSwitchMenuPlayEn);
        totalScoreTextViewString = getString(R.string.totalScoreTextViewPlayEn);
        questionTextViewString = getString(R.string.questionTextViewPlayEn);
        nextQuestionButton.setText(R.string.nextButtonMenuPlayEn);
        tryAgainButton.setText(R.string.tryAgainButtonPlayEn);
        questionScoreView.setText(R.string.questionScoreTextViewPlayEn);
        totalScoreNextView.setText(R.string.totalScoreNextTextViewPlayEn);
        invalidInputToast = getString(R.string.invalidInputToastPlayEn);
        youAnsweredText = getString(R.string.youAnsweredTextPlayEn);
        timeExpiredText = getString(R.string.timeExpiredTextPlayEn);
        appLanguage = Locale.ENGLISH;
        remainings = getString(R.string.superPowerRemainingEn);
        rights = getString(R.string.superPowerRightAnwerEn);

    }

    private void setViewForRomanianLanguage(){
        microphoneSwitch.setText(R.string.microphoneSwitchMenuPlayRou);
        totalScoreTextViewString = getString(R.string.totalScoreTextViewPlayRou);
        questionTextViewString = getString(R.string.questionTextViewPlayRou);
        nextQuestionButton.setText(R.string.nextButtonMenuPlayRou);
        tryAgainButton.setText(R.string.tryAgainButtonPlayRou);
        questionScoreView.setText(R.string.questionScoreTextViewPlayRou);
        totalScoreNextView.setText(R.string.totalScoreNextTextViewPlayRou);
        invalidInputToast = getString(R.string.invalidInputToastPlayRou);
        youAnsweredText = getString(R.string.youAnsweredTextPlayRou);
        timeExpiredText = getString(R.string.timeExpiredTextPlayRou);
        appLanguage = Locale.getDefault();
        remainings = getString(R.string.superPowerRemainingRou);
        rights = getString(R.string.superPowerRightAnwerRou);
    }

    private void setTextForViewsWithComplexText() {
        totalScoreView.setText(totalScoreTextViewString + "   " + totalPoints);
        questionCounterTextView.setText(questionTextViewString + "   " + questionCounter + " / 10");
        btn_superpower.setText("50 - 50 \n "+ LoggedUserData.loggedSuperPowerFiftyFifty+" "+ remainings);
        btn_RightAnswer.setText(rights+LoggedUserData.loggedSuperPowerCorrectAnswer+" "+ remainings);
    }

    private void chooseLanguage(){
        switch (LoggedUserData.language){
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
        if(LoggedUserData.dailyQuestion){
            btn_superpower.setVisibility(View.GONE);
            btn_RightAnswer.setVisibility(View.GONE);

        }

    }

    private void setEnabledForBonusButtons(){
        if(LoggedUserData.loggedSuperPowerFiftyFifty>0){
            btn_superpower.setEnabled(true);

        }else{
            btn_superpower.setEnabled(false);
            btn_superpower.setTextColor(Color.GRAY);
        }
        if(LoggedUserData.loggedSuperPowerCorrectAnswer>0){
            btn_RightAnswer.setEnabled(true);

        }else{
            btn_RightAnswer.setEnabled(false);
            btn_RightAnswer.setTextColor(Color.GRAY);
        }
    }

    private void updateProgressBar(){
        progressBar.setProgress(progressBarPercent);
    }

    private void speechInitialize(){
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//deschide o activitate ce solicita utilizatorului sa vorbeasca si trimite mesajul catre un SpeechRecognizer.
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, appLanguage);

    }

    private String getLanguage(String param){
        if(LoggedUserData.language.equals("english")){
            return param.concat("En");
        }else{
            return param;
        }
    }
    private void readQuestionData(final  FirebaseCallback firebaseCallback){
        FirebaseHelper.questionDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nodeQuestion = getLanguage("question");
                if(questions.isEmpty()) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Question question = new Question(Integer.parseInt(dataSnapshot1.getKey()),
                                dataSnapshot1.child(nodeQuestion).getValue(String.class),
                                dataSnapshot1.child("category").getValue(String.class));

                        for(Option o : optionList)
                            if(o.isValue() && o.getName().equals(question.getCategory()) || LoggedUserData.dailyQuestion) {
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

    private void readAnswersData(final  FirebaseCallback firebaseCallback){
        FirebaseHelper.answerDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String answerNode=getLanguage("answer");
                if(answers.isEmpty()) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
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

    public void setTextViewWithQuestionAndAnswers(boolean isFifty){
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
                setAnswers(answers,isFifty);
                if(optionList.get(SPEAKER).isValue()) {
                    setTextToSpeechListener();

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

    private void setAnswers(List<Answer> answers,boolean fifty) {
        setButtonsVisible();
        List<Answer> currentAnswers = new ArrayList<>();
        Random rand = new Random();
        List<Integer> randomCurrentAnswersList = new ArrayList<>();
        List<Integer> randomEliminateList=new ArrayList<>();
        int r;

        while(randomCurrentAnswersList.size() < 4){
            r = rand.nextInt(4);
            if(!randomCurrentAnswersList.contains(r)){
                randomCurrentAnswersList.add(r);

            }

        }

        if(answers.size() % 4 == 0 &&  answers.size()!=0) {
            for (Answer answer : answers) {
                if (answer.getQuestionId() == currentQuestion.getQuestionID()) {
                    currentAnswers.add(answer);

                }

            }

            btnA.setText("A: "+currentAnswers.get(randomCurrentAnswersList.get(0)).getAnswer());
            btnB.setText("B: "+currentAnswers.get(randomCurrentAnswersList.get(1)).getAnswer());
            btnC.setText("C: "+currentAnswers.get(randomCurrentAnswersList.get(2)).getAnswer());
            btnD.setText("D: "+currentAnswers.get(randomCurrentAnswersList.get(3)).getAnswer());

            int correct = setCorrectAnswer(
                    currentAnswers.get(randomCurrentAnswersList.get(0)).isCorrect(),
                    currentAnswers.get(randomCurrentAnswersList.get(1)).isCorrect(),
                    currentAnswers.get(randomCurrentAnswersList.get(2)).isCorrect(),
                    currentAnswers.get(randomCurrentAnswersList.get(3)).isCorrect()
            );
            if(fifty){
                generateRandomAnswerForEliminate(rand, randomEliminateList, correct);
                set2WrongAnswerAsInvisible(randomEliminateList);
            }

        }
    }

    private void set2WrongAnswerAsInvisible(List<Integer> randomEliminateList) {
        for(Integer i: randomEliminateList){
            switch (i){
                case 0:btnA.setVisibility(View.INVISIBLE);break;
                case 1:btnB.setVisibility(View.INVISIBLE);break;
                case 2:btnC.setVisibility(View.INVISIBLE);break;
                case 3:btnD.setVisibility(View.INVISIBLE);break;
            }
        }
    }

    private void generateRandomAnswerForEliminate(Random rand, List<Integer> randomEliminateList, int correct) {
        int r;
        while(randomEliminateList.size()!=2) {
            r = rand.nextInt(4);
            if (r != correct && !randomEliminateList.contains(r)) {
                randomEliminateList.add(r);
            }
        }
    }

    private void setButtonsVisible() {
        btnA.setVisibility(View.VISIBLE);
        btnB.setVisibility(View.VISIBLE);
        btnC.setVisibility(View.VISIBLE);
        btnD.setVisibility(View.VISIBLE);
    }


    private int setCorrectAnswer(boolean first,boolean second,boolean third,boolean fourth){
        if(first){
            correctAnswer = "A";
            return 0;
        }
        if(second){
            correctAnswer = "B";
            return 1;
        }
        if(third){
            correctAnswer = "C";
            return 2;
        }
        if(fourth){
            correctAnswer = "D";

        }
        return 3;
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
                voiceInput = result.get(0);
                Log.d("input",result.get(0));
                if(nextQuestionButton.getVisibility() == View.GONE) {

                    switch (LoggedUserData.language){
                        case "english":
                            afterQuestionSpeechInputEn(voiceInput);
                            break;
                        case "romanian":
                            afterQuestionSpeechInputRou(voiceInput);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + LoggedUserData.language);
                    }

                }else{
                    switch (LoggedUserData.language){
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

    private void afterQuestionSpeechInputEn(String voiceInput){
        switch (voiceInput) {
            case "A":
            case "a":
                selectedThroughVoiceOption=btnA;
                break;
            case "B":
            case "b":
                selectedThroughVoiceOption=btnB;
                break;
            case "C":
            case "c":
                selectedThroughVoiceOption=btnC;
                break;
            case "D":
            case "d":
                selectedThroughVoiceOption=btnD;
                break;
            case "Fifty fifty":
            case "fifty fifty":
                if(loggedSuperPowerFiftyFifty > 0){
                    btn_superpower.performClick();
                }
                speechRecognizer.destroy();
                getSpeechInput();
                return;
            case "Right answer":
            case "right answer":
                case "right Answer":
                if(loggedSuperPowerCorrectAnswer > 0){
                    btn_RightAnswer.performClick();
                }
                speechRecognizer.destroy();
                getSpeechInput();
                return;
            default:
                Toast.makeText(this,invalidInputToast, Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();
                return;

        }
        clicked(selectedThroughVoiceOption);

    }


    private void afterQuestionSpeechInputRou(String voiceInput){
        switch (voiceInput) {
            case "A":
            case "a":
                selectedThroughVoiceOption=btnA;
                break;
            case "B":
            case "b":
                selectedThroughVoiceOption=btnB;
                break;
            case "C":
            case "c":
                selectedThroughVoiceOption=btnC;
                break;
            case "D":
            case "d":
                selectedThroughVoiceOption=btnD;
                break;
            case "50 50":
                if(loggedSuperPowerFiftyFifty > 0){
                    btn_superpower.performClick();
                }
                speechRecognizer.destroy();
                getSpeechInput();
                return;
            case "Răspuns corect":
            case "răspuns corect":
                if(loggedSuperPowerCorrectAnswer > 0){
                    btn_RightAnswer.performClick();
                }
                speechRecognizer.destroy();
                getSpeechInput();
                return;
            default:
                Toast.makeText(this,invalidInputToast, Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();
                return;

        }
        clicked(selectedThroughVoiceOption);

    }

    private void afterNextSpeechInputEn(String voiceInput){
        switch (voiceInput) {
            case "Next":
            case "next":
                nextQuestionButton.callOnClick();
                break;
            case "Try again":
            case "try again":
                if(tryAgainButton.getVisibility() == View.VISIBLE){
                    tryAgainButton.callOnClick();
                    return;

                }
            default:
                Toast.makeText(this,invalidInputToast, Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();

        }

    }

    private void afterNextSpeechInputRou(String voiceInput){
        switch (voiceInput) {
            case "Continuă":
            case "continuă":
                nextQuestionButton.callOnClick();
                break;
            case "Încearcă din nou":
            case "încearcă din nou":
                if(tryAgainButton.getVisibility() == View.VISIBLE){
                    tryAgainButton.callOnClick();
                    return;

                }
            default:
                Toast.makeText(this,invalidInputToast, Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();

        }

    }


    public void clicked(View view) {
        setEnabledForAnswersButtons(false);
        setEnabledFalseForBonusButtons();
        if(optionList.get(MIC).isValue()) {
            speechRecognizer.destroy();
        }
        answerWasSet = true;

        ((SubmitButton) view).startAnimation();
        getUserAnswer(view);
        if(userAnswer.equals(correctAnswer)){
            setExtraTimeForMicrophone();
            calculatePoints();
            userAnswerIsCorrect = true;
            questionCounter++;
            if(questionCounter == TOTAL_QUESTION_TO_WIN_GAME){
                LoggedUserData.loggedUserPoints = LoggedUserData.loggedUserPoints + totalPoints*2;
                ++LoggedUserData.loggedSuperPowerFiftyFifty;
                ++LoggedUserData.loggedGamesWon;
                increaseRightAnswerSuperpower();
                sendToDatabase();
            }
        }
        else{
            ((SubmitButton) view).setBtn_LineColor(Color.RED);
            ((SubmitButton) view).setAnimationColor(Color.RED);
            LoggedUserData.loggedUserPoints = LoggedUserData.loggedUserPoints + totalPoints;
            sendToDatabase();
            userAnswerIsCorrect = false;

        }
        delay(3000);
    }

    private void increaseRightAnswerSuperpower() {
        if(LoggedUserData.loggedGamesWon%2==0){
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
        if(voiceInput == null){
            time = 0;

        }else{
            time = 2;
        }
    }

    private void getUserAnswer(View view) {
        switch(view.getId()){
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
        map.put("gamesWon",LoggedUserData.loggedGamesWon);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower",LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("superpowerCorrectAnswer",LoggedUserData.loggedSuperPowerCorrectAnswer);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("dailyQuestionTime", LoggedUserData.loggedUserDailyQuestionTime);
        map.put("luckModeTime", LoggedUserData.loggedUserLuckModeTime);
    }

    private void generateRandomPrizeForDailyQuestion(){
        Random rand = new Random();
        int r = rand.nextInt(2);

        if(userAnswerIsCorrect) {
            if (r == 0) {
                questionScoreView.setText("You won a fifty-fifty!");
                loggedSuperPowerFiftyFifty++;

            } else {
                questionScoreView.setText("You won a right answer!");
                loggedSuperPowerCorrectAnswer++;

            }
            sendToDatabase();
        }else{
            questionScoreView.setText("You answer wrong!");

        }

    }

    private void delay(int delay) {
        new Handler().postDelayed(() -> {
            progressBarPercent = 0;
            progressBar.setProgress(progressBarPercent);
            if(LoggedUserData.dailyQuestion){
                generateRandomPrizeForDailyQuestion();
            }
            updateUIAfterAnswerToTheQuestion();
            setTextAfterAnswerToTheQuestion();
            if(optionList.get(MIC).isValue()) {
                getSpeechInput();

            }

        }, delay);

    }
    private void timer(){
        setEnabledForAnswersButtons(true);
        if(!dailyQuestion){
            setEnabledForBonusButtons();
        }
        final long prev[]= new long[1];
        prev[0]=0;
        new CountDownTimer(30000, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                if(answerWasSet){
                    question.setText(youAnsweredText);
                    cancel();
                    return;

                }
                if(millisUntilFinished / 1000 % 3 ==0 && millisUntilFinished / 1000 != prev[0]){
                    prev[0] = millisUntilFinished / 1000;
                    progressBarPercent+=10;
                    updateProgressBar();
                }
                if(millisUntilFinished >= 10000){
                    timerView.setText(String.valueOf(millisUntilFinished / 1000));

                }else{
                    timerView.setText(SPACESTRING + millisUntilFinished / 1000);

                }

            }

            @Override
            public void onFinish() {
                question.setText(timeExpiredText);
                LoggedUserData.loggedUserPoints = LoggedUserData.loggedUserPoints + totalPoints;
                sendToDatabase();
                userAnswerIsCorrect = false;
                setTextAfterAnswerToTheQuestion();
                updateUIAfterAnswerToTheQuestion();

            }

        }.start();

    }

    private boolean isGameFinished(){
        if((questionCounter == 11 || !userAnswerIsCorrect) || (questionCounter == 2 && LoggedUserData.dailyQuestion)){
            LoggedUserData.dailyQuestion = false;
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            finishAndRemoveTask();
            return true;
        }
        return false;

    }

    private void updateUIAfterAnswerToTheQuestion(){
        infoLayout.setVisibility(View.GONE);
        materialCardView.setVisibility(View.GONE);
        firstLineButtonsLayout.setVisibility(View.GONE);
        nextQuestionButton.setVisibility(View.VISIBLE);
        if((questionCounter == TOTAL_QUESTION_TO_WIN_GAME || !userAnswerIsCorrect) && !LoggedUserData.dailyQuestion) {
            tryAgainButton.setVisibility(View.VISIBLE);

        }
        microphoneSwitch.setVisibility(View.VISIBLE);
        questionScoreView.setVisibility(View.VISIBLE);

        if(!LoggedUserData.dailyQuestion) {
            btn_superpower.setVisibility(View.GONE);
            btn_RightAnswer.setVisibility(View.GONE);
            questionScoreViewScore.setVisibility(View.VISIBLE);
            totalScoreNextView.setVisibility(View.VISIBLE);
            totalScoreNextViewPoints.setVisibility(View.VISIBLE);

        }

    }

    private void setTextAfterAnswerToTheQuestion(){
        questionScoreViewScore.setText(String.valueOf(time));
        if(questionCounter == TOTAL_QUESTION_TO_WIN_GAME){
            totalScoreNextViewPoints.setText(totalPoints + " X 2");

        }
        else{
            totalScoreNextViewPoints.setText(String.valueOf(totalPoints));

        }

    }

    private void setListenerForMicrophoneSwitch(){
        microphoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            optionList.get(MIC).setValue(isChecked);
            SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            if(!isChecked){
                speechRecognizer.destroy();
            }else{
                if(speechIntent == null){
                    speechInitialize();
                }
                getSpeechInput();
            }
            editor.putString("mic",String.valueOf(isChecked));
            editor.apply();
        });
    }

    private void updateUIForTheNextQuestion(){
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
        if(!dailyQuestion) {
            btn_superpower.setVisibility(View.VISIBLE);
            btn_RightAnswer.setVisibility(View.VISIBLE);

        }

    }

    public void nextQuestionSetup(View view){
        if(optionList.get(MIC).isValue()) {
            speechRecognizer.destroy();
        }
        if(isGameFinished()){
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
        setAnswers(answers,false);
        answerWasSet = false;
        voiceInput = null;
        selectedThroughVoiceOption = null;
        if(!optionList.get(SPEAKER).isValue()){
            if(optionList.get(MIC).isValue()) {
                getSpeechInput();
            }
            timer();

         }else{
            speak(obtainQuestionSpeech(), QUEUE_ADD);

         }

    }

    public void tryAgain(View view){
        Intent intent = new Intent(this, GameSettingsActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    private void setEnabledForAnswersButtons(boolean status){
        btnA.setEnabled(status);
        btnB.setEnabled(status);
        btnC.setEnabled(status);
        btnD.setEnabled(status);

    }

    private void setEnabledFalseForBonusButtons(){
        btn_superpower.setEnabled(false);
        btn_RightAnswer.setEnabled(false);

    }

}

