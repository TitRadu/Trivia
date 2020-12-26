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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    String userAnswer, correctAnswer, voiceInput;
    Button btnA, btnB, btnC, btnD, selectedThroughVoiceOption, microphoneInGameButton, nextQuestionButton;
    TextView question, questionCounter, timerView, totalScoreView;
    int answerCounter, standardButtonColor;
    boolean answerCheck;
    boolean touchDisabled;
    List<Question> questions;
    List<Answer> answers;
    Question currentQuestion;
    HashMap<String, Object> map = new HashMap<>();
    int totalPoints = 0;
    int time = 0;
    Intent speechIntent = null;
    SpeechRecognizer speechRecognizer;
    LinearLayout firstLineButtonsLayout, secondLineButtonsLayout, microphoneLayout;

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
        standardButtonColor = Color.LTGRAY;
        touchDisabled = false;
        selectedThroughVoiceOption = null;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        firstLineButtonsLayout = findViewById(R.id.firstLineButtonsLayout);
        secondLineButtonsLayout = findViewById(R.id.secondLineButtonsLayout);
        microphoneLayout = findViewById(R.id.microphoneLayout);
        microphoneInGameButton = findViewById(R.id.microphoneInGameBtn);
        if(LoggedUserConstants.userMicrophone){
            microphoneInGameButton.setText("Turn off microphone");

        }else{
            microphoneInGameButton.setText("Turn on microphone");


        }
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        setTextViewWithQuestionAndAnswers();
        if(LoggedUserConstants.userMicrophone) {
            speechInitialize();

        }

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

            btnA.setText(currentAnswers.get(randomCurrentAnswersList.get(0)).getAnswer());
            btnB.setText(currentAnswers.get(randomCurrentAnswersList.get(1)).getAnswer());
            btnC.setText(currentAnswers.get(randomCurrentAnswersList.get(2)).getAnswer());
            btnD.setText(currentAnswers.get(randomCurrentAnswersList.get(3)).getAnswer());

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

        if(userAnswer.equals(correctAnswer)){
            double d = Double.parseDouble(timerView.getText().toString());
            time = (int) d;
            totalPoints = totalPoints + time;
            totalScoreView.setText("Total score:" + totalPoints);
            Log.d("Punctaj Intrebare" + answerCounter,String.valueOf(time));
            Log.d("Punctaj Total",String.valueOf(totalPoints));

            answerCheck = true;
            question.setText("Corect Answer!");
            view.setBackgroundColor(Color.GREEN);
            answerCounter++;
            if(answerCounter == 11){
                map = new HashMap<>();
                map.put("email", LoggedUserConstants.loggedUserEmail);
                map.put("userName",LoggedUserConstants.loggedUserName);
                map.put("password", LoggedUserConstants.loggedUserPassword);
                map.put("points", LoggedUserConstants.loggedUserPoints + totalPoints);
                FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);
                LoggedUserConstants.loggedUserPoints = LoggedUserConstants.loggedUserPoints + totalPoints;
                questionCounter.setText("Ai castigat!");

            }

        }
        else{
            map = new HashMap<>();
            map.put("email", LoggedUserConstants.loggedUserEmail);
            map.put("userName",LoggedUserConstants.loggedUserName);
            map.put("password", LoggedUserConstants.loggedUserPassword);
            map.put("points", LoggedUserConstants.loggedUserPoints + totalPoints);
            FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);
            LoggedUserConstants.loggedUserPoints = LoggedUserConstants.loggedUserPoints + totalPoints;
            answerCheck = false;
            question.setText("Wrong Answer!");
            view.setBackgroundColor(Color.RED);

        }

        delay(3000);

    }

    private void delay(int delay) {
        new Handler().postDelayed(() -> {
            if(!answerCheck) {
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                finishAndRemoveTask();
                return;

            }
            hideQuestionSetup();
            if(LoggedUserConstants.userMicrophone) {
                getSpeechInput();

            }

        }, delay);

    }

    private void timer(){
        new CountDownTimer(30000, 1) {

           @Override
           public void onTick(long millisUntilFinished) {
               if(touchDisabled){
                   timerView.setText("You answered!");
                   cancel();
                   return;

               }
               timerView.setText(millisUntilFinished / 1000 + "." + millisUntilFinished % 1000);

           }

           @Override
           public void onFinish() {
               timerView.setText("Time expired!");
               map = new HashMap<>();
               map.put("email", LoggedUserConstants.loggedUserEmail);
               map.put("userName",LoggedUserConstants.loggedUserName);
               map.put("password", LoggedUserConstants.loggedUserPassword);
               map.put("points", LoggedUserConstants.loggedUserPoints + totalPoints);
               FirebaseHelper.userDatabaseReference.child(LoggedUserConstants.loggedUserKey).setValue(map);
               LoggedUserConstants.loggedUserPoints = LoggedUserConstants.loggedUserPoints + totalPoints;
               finishAndRemoveTask();

           }

       }.start();

    }

    private boolean verifyGameState(){
        if(answerCounter == 11){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            finishAndRemoveTask();
            return false;
        }

        return true;

    }

    private void hideQuestionSetup(){
        timerView.setVisibility(View.GONE);
        question.setVisibility(View.GONE);
        firstLineButtonsLayout.setVisibility(View.GONE);
        secondLineButtonsLayout.setVisibility(View.GONE);
        microphoneLayout.setVisibility(View.GONE);
        microphoneInGameButton.setVisibility(View.VISIBLE);
        nextQuestionButton.setVisibility(View.VISIBLE);

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
        timerView.setVisibility(View.VISIBLE);
        question.setVisibility(View.VISIBLE);
        firstLineButtonsLayout.setVisibility(View.VISIBLE);
        secondLineButtonsLayout.setVisibility(View.VISIBLE);
        microphoneLayout.setVisibility(View.VISIBLE);

    }

    public void nextQuestionSetup(View view){
        if(LoggedUserConstants.userMicrophone) {
            speechRecognizer.destroy();

        }
        if(!verifyGameState()){
            return;

        }
        showQuestionSetup();
        btnA.setBackgroundColor(standardButtonColor);
        btnB.setBackgroundColor(standardButtonColor);
        btnC.setBackgroundColor(standardButtonColor);
        btnD.setBackgroundColor(standardButtonColor);
        questionCounter.setText("Intrebarea " + answerCounter + " din 10");
        seteazaIntrebare(questions);
        seteazaRaspunsuri(answers);
        touchDisabled = false;
        selectedThroughVoiceOption = null;
        if(LoggedUserConstants.userMicrophone) {
            getSpeechInput();

        }
        timer();

    }

}

