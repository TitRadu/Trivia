package com.example.triviaapp.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LuckPlayModeActivity extends AppCompatActivity {
    TextView countTextView, fiftyCountTextTextView, fiftyCountValueTextView, rightCountTextTextView, rightCountValueTextView;
    Button firstOptionButton, secondOptionButton, thirdOptionButton, fourthOptionButton, collectButton;
    String collectQuestionTextViewPopUpTextString;
    String lostPrizeTextViewPopUpTextString;
    String collectButtonPopUpTextString;
    String lostPrizeButtonPopUpTextString;

    int count = 1;
    int rightAnswerCount = 0;
    int fiftyFiftyCount = 0;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luck_play_mode);
        initializeViews();
        setOnClickListeners();

    }

    private void initializeViews(){
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
        rightCountTextTextView.setText(R.string.rightAnswerTextLuckEn);
        collectButton.setText(R.string.collectButtonTextLuckEn);
        collectQuestionTextViewPopUpTextString = getString(R.string.collectQuestionTextLuckEn);
        lostPrizeTextViewPopUpTextString = getString(R.string.lostPrizeQuestionTextLuckEn);
        collectButtonPopUpTextString = getString(R.string.collectButtonTextLuckEn);
        lostPrizeButtonPopUpTextString = getString(R.string.exitButtonTextLuckEn);

    }


    private void setViewForRomanianLanguage() {
        rightCountTextTextView.setText(R.string.rightAnswerTextLuckRou);
        collectButton.setText(R.string.collectButtonTextLuckRou);
        collectQuestionTextViewPopUpTextString = getString(R.string.collectQuestionTextLuckRou);
        lostPrizeTextViewPopUpTextString = getString(R.string.lostPrizeQuestionTextLuckRou);
        collectButtonPopUpTextString = getString(R.string.collectButtonTextLuckRou);
        lostPrizeButtonPopUpTextString = getString(R.string.exitButtonTextLuckRou);

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

    private void setOnClickListeners(){
        collectButton.setOnClickListener(v -> collectPopUp("Collect"));

    }

    public void setOptionButtonsListener(View view){
        setEnabledForOptionButtons(false);

        Random rand = new Random();
        List<Integer> randomOptionList = new ArrayList<>();
        int r;

        while(randomOptionList.size() < 4){
            r = rand.nextInt(4);
            if(!randomOptionList.contains(r)){
                randomOptionList.add(r);

            }

        }


        int bonus = 2;
        switch(view.getId()){
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
        Log.d("option",String.valueOf(bonus));
        switch (bonus){
            case 0:((Button)view).setText("R. A.");
                    rightAnswerCount++;
                    rightCountValueTextView.setText(String.valueOf(rightAnswerCount));
                    break;
            case 1:((Button)view).setText("F. F.");
                    fiftyFiftyCount++;
                    fiftyCountValueTextView.setText(String.valueOf(fiftyFiftyCount));
                    break;
            case 2:((Button)view).setText("None");
                break;
            case 3:((Button)view).setText("Wrong");
                delay(3000, "Wrong");
                return;

        }

        delay(3000, "Collect");

    }

    private void setEnabledForOptionButtons(boolean status){
        collectButton.setEnabled(status);
        firstOptionButton.setEnabled(status);
        secondOptionButton.setEnabled(status);
        thirdOptionButton.setEnabled(status);
        fourthOptionButton.setEnabled(status);

    }

    private void delay(int delay, String control) {
        new Handler().postDelayed(() -> {
            switch(control){
                case "Collect":
                    count++;
                    countTextView.setText(String.valueOf(count));
                    firstOptionButton.setText("1");
                    secondOptionButton.setText("2");
                    thirdOptionButton.setText("3");
                    fourthOptionButton.setText("4");
                    setEnabledForOptionButtons(true);
                    break;
                case "Wrong" :
                    collectPopUp("Wrong");
                    break;

            }
        }, delay);

    }

    private void collectPopUp(String control){
        dialogBuilder = new AlertDialog.Builder(this);
        View questionPopUpView = getLayoutInflater().inflate(R.layout.template_question_pop_up, null);
        ImageView xImageViewPopUp = questionPopUpView.findViewById(R.id.xImageViewPopUp);
        ImageView questionIcon = questionPopUpView.findViewById(R.id.templateImageViewPopUp);
        TextView infoTextViewPopUp = questionPopUpView.findViewById(R.id.templateInfoTextViewPopUp);
        Button collectButtonPopUp = questionPopUpView.findViewById(R.id.continueButtonPopUp);

        switch(control){
            case "Collect":
                infoTextViewPopUp.setText(collectQuestionTextViewPopUpTextString);
                collectButtonPopUp.setText(collectButtonPopUpTextString);
                break;
            case "Wrong" :
                xImageViewPopUp.setVisibility(View.GONE);
                infoTextViewPopUp.setText(lostPrizeTextViewPopUpTextString);
                collectButtonPopUp.setText(lostPrizeButtonPopUpTextString);
                break;

        }

        dialogBuilder.setView(questionPopUpView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        xImageViewPopUp.setOnClickListener((v) -> dialog.dismiss());
        collectButtonPopUp.setOnClickListener((v) -> continueAction(control));

    }

    private void continueAction(String control){
        if(control.equals("Collect")){
            updateData();

        }
        dialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

    private void updateData(){
        LoggedUserData.loggedSuperPowerCorrectAnswer = LoggedUserData.loggedSuperPowerCorrectAnswer + rightAnswerCount;
        LoggedUserData.loggedSuperPowerFiftyFifty = LoggedUserData.loggedSuperPowerFiftyFifty + fiftyFiftyCount;
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

    }

    private HashMap<String, Object> populateMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", LoggedUserData.loggedUserEmail);
        map.put("gamesWon",LoggedUserData.loggedGamesWon);
        map.put("password", LoggedUserData.loggedUserPassword);
        map.put("points", LoggedUserData.loggedUserPoints);
        map.put("superpower",LoggedUserData.loggedSuperPowerFiftyFifty);
        map.put("superpowerCorrectAnswer",LoggedUserData.loggedSuperPowerCorrectAnswer);
        map.put("userName", LoggedUserData.loggedUserName);
        map.put("dailyQuestionTime", LoggedUserData.loggedUserDailyQuestionTime);
        map.put("luckModeTime", LoggedUserData.loggedUserLuckModeTime);
        return map;

    }

}