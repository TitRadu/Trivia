package com.example.triviaapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {
    String userAnswer;
    String correctAnswer;
    Button A,B,C,D,voiceButton;
    TextView question;
    TextView questionCounter;
    int answerCounter;
    int standardButtonColor;
    boolean answerCheck;
    Intent intent;
    boolean touchDisabled;
    boolean voiceButtonDisabled;
    String voiceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setViews();
    }

    public void setViews(){
        userAnswer = new String("");
        correctAnswer = new String("C");
        A = findViewById(R.id.varA);
        B = findViewById(R.id.varB);
        C = findViewById(R.id.varC);
        D = findViewById(R.id.varD);
        question = findViewById(R.id.question);
        questionCounter = findViewById(R.id.questionCounter);
        answerCounter = 1;
        standardButtonColor = Color.LTGRAY;
        intent = new Intent(this,MainActivity.class);
        touchDisabled = false;
        voiceButtonDisabled = false;
        voiceButton = null;

    }

    public void clicked(View view) {
        if(touchDisabled == true){
            return;
        }else{
            touchChange(true);

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

        //Aici va veni comparat cu raspunsul corect din baza de date.
        if(userAnswer.equals(correctAnswer)){
            answerCheck = true;
            question.setText("Corect Answer!");
            ((Button)view).setBackgroundColor(Color.GREEN);
            answerCounter++;
            if(answerCounter == 11){
                questionCounter.setText("Ai castigat!");

            }

        }
        else{
            answerCheck = false;
            question.setText("Wrong Answer!");
            ((Button)view).setBackgroundColor(Color.RED);

        }

        voiceButton = A;
        voiceButtonDisabled = true;

        new Handler().postDelayed(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run(){
                if(answerCounter == 11){
                    finishAndRemoveTask();
                }
                if(!answerCheck) {
                    finishAndRemoveTask();

                }
                A.setBackgroundColor(standardButtonColor);
                B.setBackgroundColor(standardButtonColor);
                C.setBackgroundColor(standardButtonColor);
                D.setBackgroundColor(standardButtonColor);
                questionCounter.setText("Intrebarea " + answerCounter + " din 10");
                question.setText("Intrebare");
                touchChange(false);
                voiceButton = null;
                voiceButtonDisabled = false;

            }

        }, 7000);

    }

    @Override
    public boolean onTouchEvent(MotionEvent m){
        if(touchDisabled){
            return true;
        }
        return false;

    }

    public void touchChange(boolean value){
        touchDisabled = value;

    }

    public void getSpeechInput(View view) {
        if(voiceButton == null){
            voiceButtonDisabled = true;
        }else{
            if (voiceButtonDisabled == true) {
                return;
            }else{
                voiceButtonDisabled = true;

            }

        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent,10);

        }else{
            Toast.makeText(this,"Your device Don't Support Speech Input", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    voiceInput = result.get(0);
                    if(voiceInput.equals("a") || voiceInput.equals("A")) {
                        voiceButton = A;

                    }else {
                        if(voiceInput.equals("b") || voiceInput.equals("B")){
                            voiceButton = B;

                        }else{
                            if(voiceInput.equals("c") || voiceInput.equals("C")){
                                voiceButton = C;

                            }else{
                                if(voiceInput.equals("d") || voiceInput.equals("D")){
                                    voiceButton = D;

                                }else{
                                    Toast.makeText(this,"Please say a valid input!!!", Toast.LENGTH_SHORT).show();
                                    voiceButton = A;
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

}

