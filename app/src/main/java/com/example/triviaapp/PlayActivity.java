package com.example.triviaapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    String userAnswer, correctAnswer, voiceInput;
    Button btnA, btnB, btnC, btnD, voiceButton;
    TextView question, questionCounter;
    int answerCounter, standardButtonColor;
    boolean answerCheck;
    Intent intent;
    boolean touchDisabled, voiceButtonDisabled;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    List<Question> questions;
    List<Answer> answers;
    Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setViews();

    }

    public void setViews(){
        userAnswer = "";
        btnA = findViewById(R.id.varA);
        btnB = findViewById(R.id.varB);
        btnC = findViewById(R.id.varC);
        btnD = findViewById(R.id.varD);
        question = findViewById(R.id.question);
        questionCounter = findViewById(R.id.questionCounter);
        answerCounter = 1;
        standardButtonColor = Color.LTGRAY;
        intent = new Intent(this,PlayActivity.class);
        touchDisabled = false;
        voiceButtonDisabled = false;
        voiceButton = null;
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        setTextViewWithQuestionAndAnswers();

    }

    private void readQuestionData(final FirebaseCallback firebaseCallback){
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Questions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(questions.isEmpty()) {
                    for (int i = 1; i < 5   ; i++) {
                        Question q = new Question
                                (i, dataSnapshot.child(String.valueOf(i)).child("question").getValue(String.class));
                        questions.add(q);
                        Log.d("lista intrebari",questions.size()+"");

                    }
                    firebaseCallback.onCallback(questions);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAnswersData(final  FirebaseCallback firebaseCallback){
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Answers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(answers.isEmpty()) {
                    for (int i = 1; i < 17; i++) {
                        Answer a = new Answer
                                (i,
                                        dataSnapshot.child(String.valueOf(i)).child("answer").getValue(String.class),
                                        dataSnapshot.child(String.valueOf(i)).child("correct").getValue(Boolean.class),
                                        dataSnapshot.child(String.valueOf(i)).child("questionId").getValue(Integer.class)
                                );
                        answers.add(a);
                        //Log.d("zuzu", a.getAnswer());
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
     Log.d("apeluri","ma apelez"+answerCounter +"dimensiune lista" +questions.size());
        readQuestionData(new FirebaseCallback() {
            @Override
            public void onCallback(List<Question> questions) {
                seteazaIntrebare(questions);
            }
            @Override
            public void onCallbackAnswers(List<Answer> answers) {

            }
        });
        readAnswersData(new FirebaseCallback() {
            @Override
            public void onCallback(List<Question> questions) {

            }

            @Override
            public void onCallbackAnswers(List<Answer> answers) {
                Log.d("size",answers.size()+"");
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

    public void getSpeechInput(View view) {
        if(voiceButtonDisabled == true){
            return;

        }else{
            voiceButtonDisabled = true;

        }

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,10);

        }else{
            Toast.makeText(this,"Your device Don't Support Speech Input", Toast.LENGTH_SHORT).show();

        }

    }




    @Override//Aici verificam daca ceea ce am rostit este un raspuns valid.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput = result.get(0);
                    if(voiceInput.equals("a") || voiceInput.equals("A")) {
                        voiceButton = btnA;

                    }else {
                        if(voiceInput.equals("b") || voiceInput.equals("B")){
                            voiceButton = btnB;

                        }else{
                            if(voiceInput.equals("c") || voiceInput.equals("C")){
                                voiceButton = btnC;

                            }else{
                                if(voiceInput.equals("d") || voiceInput.equals("D")){
                                    voiceButton = btnD;

                                }else{
                                    Toast.makeText(this,"Please say a valid input!!!", Toast.LENGTH_SHORT).show();
                                    //linie stearsa
                                    voiceButtonDisabled = false;
                                    return;
                                }
                            }

                        }

                    }

                    clicked(voiceButton);

                }else {
                    voiceButtonDisabled = false;

                }
                break;

        }

    }

    public void clicked(View view) {
        if(touchDisabled == true){
            return;
        }else{
            touchDisabled = true;


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

        voiceButtonDisabled = true;

        if(userAnswer.equals(correctAnswer)){
            answerCheck = true;
            question.setText("Corect Answer!");
            view.setBackgroundColor(Color.GREEN);
            answerCounter++;
            if(answerCounter == 10){
                questionCounter.setText("Ai castigat!");

            }

        }
        else{
            answerCheck = false;
            question.setText("Wrong Answer!");
            view.setBackgroundColor(Color.RED);

        }

        delay(3000);

    }

    private void delay(int delay) {
        new Handler().postDelayed(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run(){
                if(answerCounter == 10){
                    finishAndRemoveTask();
                }
                if(!answerCheck) {
                    finishAndRemoveTask();

                }

                btnA.setBackgroundColor(standardButtonColor);
                btnB.setBackgroundColor(standardButtonColor);
                btnC.setBackgroundColor(standardButtonColor);
                btnD.setBackgroundColor(standardButtonColor);
                questionCounter.setText("Intrebarea " + answerCounter + " din 10");
                seteazaIntrebare(questions);
                seteazaRaspunsuri(answers);
                touchDisabled = false;
                voiceButton = null;
                voiceButtonDisabled = false;


            }

        }, delay);
    }

}

