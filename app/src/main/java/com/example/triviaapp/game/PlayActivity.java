package com.example.triviaapp.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.triviaapp.Answer;
import com.example.triviaapp.FirebaseCallback;
import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.Question;
import com.example.triviaapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    public static final int TOTAL_QUESTION_TO_WIN_GAME = 11;
    String userAnswer, correctAnswer, voiceInput = null;
    Button btnA, btnB, btnC, btnD, selectedThroughVoiceOption, microphoneInGameButton, nextQuestionButton;
    TextView question, questionCounter, timerView, totalScoreView, questionScoreView, totalScoreNextView;
    ProgressBar progressBar;
    int answerCounter;
    boolean answerCheck;
    boolean touchDisabled;
    List<Question> questions;
    List<Answer> answers;
    Question currentQuestion;
    HashMap<String, Object> map = new HashMap<>();
    int totalPoints = 0;
    int time = 0;
    int progressBarPercent=0;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    LinearLayout firstLineButtonsLayout, microphoneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setViews();
        timer();
        if(LoggedUserConstants.userMicrophone) {
            getSpeechInput();
        }

    }

    public void setViews(){
        userAnswer = "";
        btnA = findViewById(R.id.varA);
        btnB = findViewById(R.id.varB);
        btnC = findViewById(R.id.varC);
        btnD = findViewById(R.id.varD);
        question = findViewById(R.id.question);
        questionCounter = findViewById(R.id.questionCounter);
        timerView = findViewById(R.id.timerView);
        totalScoreView = findViewById(R.id.totalScoreView);
        answerCounter = 1;
        touchDisabled = false;
        selectedThroughVoiceOption = null;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        firstLineButtonsLayout = findViewById(R.id.firstLineButtonsLayout);
        microphoneLayout = findViewById(R.id.microphoneLayout);
        microphoneInGameButton = findViewById(R.id.microphoneInGameBtn);
        setTextForBtnMicrophone();
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        questionScoreView = findViewById(R.id.questionScoreView);
        totalScoreNextView = findViewById(R.id.totalScoreNextView);
        progressBar = findViewById(R.id.progress_bar);


        setTextViewWithQuestionAndAnswers();
        if(LoggedUserConstants.userMicrophone) {
            speechInitialize();

        }

    }

    private void setTextForBtnMicrophone() {
        if(LoggedUserConstants.userMicrophone){
            microphoneInGameButton.setText("Turn off microphone");

        }else{
            microphoneInGameButton.setText("Turn on microphone");


        }
    }

    private void updateProgressBar(){
         progressBar.setProgress(progressBarPercent);
    }

    private void speechInitialize(){
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//deschide o activitate ce solicita utilizatorului sa vorbeasca si trimite mesajul catre un SpeechRecognizer.
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

    }

    private void readQuestionData(final  FirebaseCallback firebaseCallback){
        FirebaseHelper.questionDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(questions.isEmpty()) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Question question = new Question(Integer.parseInt(dataSnapshot1.getKey()),dataSnapshot1.child("question").getValue(String.class));
                        questions.add(question);


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
                if(answers.isEmpty()) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Answer a = new Answer
                                (Integer.parseInt(dataSnapshot1.getKey()),
                                        dataSnapshot.child(String.valueOf(dataSnapshot1.getKey())).child("answer").getValue(String.class),
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

    public void setTextViewWithQuestionAndAnswers(){
        readQuestionData(new FirebaseCallback() {
            @Override
            public void onCallbackQuestions(List<Question> questions) {
                seteazaIntrebare(questions);

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
                seteazaRaspunsuri(answers);

            }

        });

    }

    private void seteazaIntrebare(List<Question> questions) {
        Random rand = new Random();

        if (questions.size() != 0) {
            int random = rand.nextInt(questions.size());
            Log.d("random", random + "");
            currentQuestion = questions.get(random);
            question.setText(currentQuestion.getQuestion());
            questions.remove(random);
            Log.d("dimensiune lista",questions.size()+"");

        }

    }

    private void seteazaRaspunsuri(List<Answer> answers) {
        List<Answer> currentAnswers = new ArrayList<>();
        Random rand = new Random();
        List<Integer> randomCurrentAnswersList = new ArrayList<>();
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

            setCorrectAnswer(
                    currentAnswers.get(randomCurrentAnswersList.get(0)).isCorrect(),
                    currentAnswers.get(randomCurrentAnswersList.get(1)).isCorrect(),
                    currentAnswers.get(randomCurrentAnswersList.get(2)).isCorrect(),
                    currentAnswers.get(randomCurrentAnswersList.get(3)).isCorrect());
        }

    }

    private void setCorrectAnswer(boolean first,boolean second,boolean third,boolean fourth){
        if(first){
            correctAnswer = "A";

        }
        if(second){
            correctAnswer = "B";

        }
        if(third){
            correctAnswer = "C";

        }
        if(fourth){
            correctAnswer = "D";

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
                questionCounter.setText("Salut");
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
                Log.d("Speech",String.valueOf(error));
                if(error == SpeechRecognizer.ERROR_NO_MATCH) {
                    speechRecognizer.destroy();
                    getSpeechInput();

                }

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                voiceInput = result.get(0);
                if(nextQuestionButton.getVisibility() == View.GONE) {
                    afterQuestionSpeechInput(voiceInput);

                }else{
                    afterNextSpeechInput(voiceInput);

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

    private void afterQuestionSpeechInput(String voiceInput){
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
            default:
                Toast.makeText(this,"Please say a valid input!!!", Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();
                return;

        }
        clicked(selectedThroughVoiceOption);

    }

    private void afterNextSpeechInput(String voiceInput){
        switch (voiceInput) {
            case "Next":
            case "next":
                nextQuestionButton.callOnClick();
                break;
            default:
                Toast.makeText(this,"Please say a valid input!!!", Toast.LENGTH_SHORT).show();
                speechRecognizer.destroy();
                getSpeechInput();

        }

    }

    public void clicked(View view) {
        if(touchDisabled){
            return;
        }else{
            touchDisabled = true;
            if(LoggedUserConstants.userMicrophone) {
                speechRecognizer.destroy();
            }
        }
        setExtraTimeForMicrophone();
        getUserAnswer(view);
        if(userAnswer.equals(correctAnswer)){
            calculatePoints();
            answerCheck = true;
            question.setText("Corect Answer!");
            view.setBackgroundColor(Color.GREEN);
            answerCounter++;
            if(answerCounter == TOTAL_QUESTION_TO_WIN_GAME){
                LoggedUserConstants.loggedUserPoints = LoggedUserConstants.loggedUserPoints + totalPoints*2;
                sendPointsToDatabase(questionCounter, "Ai castigat!");
            }
        }
        else{
            LoggedUserConstants.loggedUserPoints = LoggedUserConstants.loggedUserPoints + totalPoints;
            sendPointsToDatabase(question, "Wrong Answer!");
            answerCheck = false;
            view.setBackgroundColor(Color.RED);
        }
        delay(3000);
    }

    private void calculatePoints() {
        double d = Double.parseDouble(timerView.getText().toString());
        time = time + (int) d;
        totalPoints = totalPoints + time;
        totalScoreView.setText("Total score:" + totalPoints);
        Log.d("Punctaj Intrebare" + answerCounter,String.valueOf(time));
        Log.d("Punctaj Total",String.valueOf(totalPoints));
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

    private void sendPointsToDatabase(TextView question, String s) {

        populateMapWithUserData();
        FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);
        question.setText(s);
    }

    private void populateMapWithUserData() {
        map = new HashMap<>();
        map.put("email", LoggedUserConstants.loggedUserEmail);
        map.put("userName", LoggedUserConstants.loggedUserName);
        map.put("password", LoggedUserConstants.loggedUserPassword);
        map.put("points", LoggedUserConstants.loggedUserPoints + totalPoints);
    }

    private void delay(int delay) {
        new Handler().postDelayed(() -> {
            if(!answerCheck) {
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                finishAndRemoveTask();
                return;

            }
            progressBarPercent = 0;
            progressBar.setProgress(progressBarPercent);
            hideQuestionSetup();
            setTextAfterAnswerQuestion();
            if(LoggedUserConstants.userMicrophone) {
                getSpeechInput();

            }

        }, delay);

    }
    private void timer(){
        final long prev[]= new long[1];
        prev[0]=0;
        new CountDownTimer(30000, 1) {

           @Override
           public void onTick(long millisUntilFinished) {
               if(touchDisabled){
                   timerView.setText("You answered!");
                   cancel();
                   return;

               }
               if(millisUntilFinished / 1000 % 3 ==0 && millisUntilFinished / 1000 != prev[0]){
                   prev[0] = millisUntilFinished / 1000;
                   progressBarPercent+=10;
                   updateProgressBar();
               }
               timerView.setText(millisUntilFinished / 1000 + "." + millisUntilFinished % 1000);

           }

           @Override
           public void onFinish() {
               timerView.setText("Time expired!");
               populateMapWithUserData();
               FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);
               LoggedUserConstants.loggedUserPoints = LoggedUserConstants.loggedUserPoints + totalPoints;
               finishAndRemoveTask();
               Intent intent = new Intent(getApplicationContext(), GameActivity.class);
               startActivity(intent);

           }

       }.start();

    }

    private boolean isGameFinished(){
        if(answerCounter == 11){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            finishAndRemoveTask();
            return true;
        }
        return false;

    }

    private void hideQuestionSetup(){
        timerView.setVisibility(View.GONE);
        question.setVisibility(View.GONE);
        firstLineButtonsLayout.setVisibility(View.GONE);
        microphoneLayout.setVisibility(View.GONE);
        microphoneInGameButton.setVisibility(View.VISIBLE);
        nextQuestionButton.setVisibility(View.VISIBLE);
        questionScoreView.setVisibility(View.VISIBLE);
        totalScoreNextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setTextAfterAnswerQuestion(){
        questionScoreView.setText("Question score:" + time);
        if(answerCounter == 11){
            totalScoreNextView.setText("Total score X 2:" + totalPoints);
        }
        else{
            totalScoreNextView.setText("Total score:" + totalPoints);
        }

    }

    public void microphoneStatus(View view){
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        if(LoggedUserConstants.userMicrophone){
            speechRecognizer.destroy();
            LoggedUserConstants.userMicrophone = false;
            microphoneInGameButton.setText("Turn on microphone");
            editor.putString("mic", "false");

        }else{
            if(speechIntent == null){
                speechInitialize();

            }
            getSpeechInput();
            LoggedUserConstants.userMicrophone = true;
            microphoneInGameButton.setText("Turn off microphone");
            editor.putString("mic", "true");

        }

        editor.apply();

    }

    private void showQuestionSetup(){
        microphoneInGameButton.setVisibility(View.GONE);
        nextQuestionButton.setVisibility(View.GONE);
        questionScoreView.setVisibility(View.GONE);
        totalScoreNextView.setVisibility(View.GONE);
        timerView.setVisibility(View.VISIBLE);
        question.setVisibility(View.VISIBLE);
        firstLineButtonsLayout.setVisibility(View.VISIBLE);
        microphoneLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void nextQuestionSetup(View view){

        if(LoggedUserConstants.userMicrophone) {
            speechRecognizer.destroy();
        }
        if(isGameFinished()){
            return;
        }
        showQuestionSetup();
        btnA.setBackgroundResource(R.drawable.custom_botton_design_corners);
        btnB.setBackgroundResource(R.drawable.custom_botton_design_corners);
        btnC.setBackgroundResource(R.drawable.custom_botton_design_corners);
        btnD.setBackgroundResource(R.drawable.custom_botton_design_corners);
        questionCounter.setText("Intrebarea " + answerCounter + " din 10");
        seteazaIntrebare(questions);
        seteazaRaspunsuri(answers);
        touchDisabled = false;
        voiceInput = null;
        selectedThroughVoiceOption = null;
        if(LoggedUserConstants.userMicrophone) {
            getSpeechInput();
        }
        timer();
    }

}

