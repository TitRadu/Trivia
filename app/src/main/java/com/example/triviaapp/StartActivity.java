package com.example.triviaapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {
    String userAnswer;
    String correctAnswer;
    Button A;Button B;Button C;Button D;
    TextView question;
    TextView questionCounter;
    int answerCounter;
    int standardButtonColor;
    boolean answerCheck;
    Intent intent;
    boolean touchDisabled;


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

            }

        }, 3000);

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

}

