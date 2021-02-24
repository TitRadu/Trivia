package com.example.triviaapp.game.ui.notifications;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.FirebaseHelper;
import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.GameSettingsActivity;
import com.example.triviaapp.game.PlayActivity;

import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.example.triviaapp.LoggedUserData.EMPTYSTRING;
import static com.example.triviaapp.LoggedUserData.MIC;
import static com.example.triviaapp.LoggedUserData.SPEAKER;
import static com.example.triviaapp.LoggedUserData.optionList;

public class NotificationsFragment extends Fragment {
    Button clasicButton, exitButton, dailyQuestionButton;
    Date date;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ImageView xImageViewPopUp;
    private TextView infoTextViewPopUp;
    private Button continueButtonPopUp;
    String dailyQuestionButtonTextString;
    String continueButtonPopUpTextString;
    String infoTextViewPopUpTextString;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchMicrophone, switchSpeaker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        initializeViews(root);
        setOnClickListeners();
        setDailyQuestionButton();
        return root;

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
        return map;
    }


    private void initializeViews(View root){
        date = new Date();
        clasicButton = root.findViewById(R.id.classicBtn);
        exitButton = root.findViewById(R.id.exitBtn);
        switchMicrophone = root.findViewById(R.id.sw_microphone);
        switchMicrophone.setChecked(optionList.get(MIC).isValue());
        switchSpeaker = root.findViewById(R.id.sw_speaker);
        switchSpeaker.setChecked(optionList.get(SPEAKER).isValue());
        dailyQuestionButton = root.findViewById(R.id.dailyQuestionButton);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        clasicButton.setText(R.string.classicButtonMenuEn);
        switchMicrophone.setText(R.string.microphoneSwitchMenuPlayEn);
        dailyQuestionButtonTextString = getString(R.string.dailyQuestionButtonMenuEn);
        infoTextViewPopUpTextString = getString(R.string.infoTextViewMenuEn);
        continueButtonPopUpTextString = getString(R.string.nextButtonMenuPlayEn);
        exitButton.setText(R.string.exitButtonMenuHelpEn);

    }


    private void setViewForRomanianLanguage(){
        clasicButton.setText(R.string.classicButtonMenuRou);
        switchMicrophone.setText(R.string.microphoneSwitchMenuPlayRou);
        dailyQuestionButtonTextString = getString(R.string.dailyQuestionButtonMenuRou);
        infoTextViewPopUpTextString = getString(R.string.infoTextViewMenuRou);
        continueButtonPopUpTextString = getString(R.string.nextButtonMenuPlayRou);
        exitButton.setText(R.string.exitButtonMenuHelpRou);

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

    }

    private void setOnClickListeners(){
        clasicButton.setOnClickListener((v) -> {openGameSettingsActivity();});
        exitButton.setOnClickListener((v) -> {exit();});
        switchMicrophone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(MIC).setValue(isChecked);
            editor.putString(optionList.get(MIC).getName(),String.valueOf(isChecked));
            editor.apply();

        });
        switchSpeaker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(SPEAKER).setValue(isChecked);
            editor.putString(optionList.get(SPEAKER).getName(),String.valueOf(isChecked));
            editor.apply();

        });
        dailyQuestionButton.setOnClickListener((v) -> dailyQuestionPopUp());

    }

    private void openGameSettingsActivity(){
        Intent intent = new Intent(getContext(), GameSettingsActivity.class);
        startActivity(intent);
        getActivity().finishAndRemoveTask();

    }

    private void exit(){
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        getActivity().finishAndRemoveTask();

    }

    private void continueToDailyQuestion(){
        dialog.dismiss();
        updateDailyQuestionTime();
        LoggedUserData.dailyQuestion = true;
        Intent intent = new Intent(getContext(), PlayActivity.class);
        startActivity(intent);
        getActivity().finishAndRemoveTask();

    }

    private void dailyQuestionPopUp(){
        dialogBuilder = new AlertDialog.Builder(getContext());
        View questionPopUpView = getLayoutInflater().inflate(R.layout.daily_question_pop_up, null);
        xImageViewPopUp = questionPopUpView.findViewById(R.id.xImageViewPopUp);
        infoTextViewPopUp = questionPopUpView.findViewById(R.id.infoTextViewPopUp);
        continueButtonPopUp = questionPopUpView.findViewById(R.id.continueButtonPopUp);

        infoTextViewPopUp.setText(infoTextViewPopUpTextString);
        continueButtonPopUp.setText(continueButtonPopUpTextString);

        dialogBuilder.setView(questionPopUpView);
        dialogBuilder.setCancelable(false);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        xImageViewPopUp.setOnClickListener((v) -> dialog.dismiss());
        continueButtonPopUp.setOnClickListener((v) -> continueToDailyQuestion());

    }

    private void timer(long miliseconds){
        final long[] hours = {300000 / 3600000};
        final String[] oneDigitHours = {""};
        final long[] minutes = {(300000 / 60000) % (60)};
        final String[] oneDigitMinutes = {""};
        final long[] seconds = {(300000 / 1000) % 60};
        final String[] oneDigitSeconds = {""};
        new CountDownTimer(miliseconds, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                hours[0] = (millisUntilFinished/3600000);
                minutes[0] = (millisUntilFinished/60000)%60;
                seconds[0] = (millisUntilFinished/1000)%60;
                if(hours[0] < 10){
                    oneDigitHours[0] = "0";

                }else{
                    oneDigitHours[0] = EMPTYSTRING;

                }
                if(minutes[0] < 10){
                    oneDigitMinutes[0] = "0";

                }else{
                    oneDigitMinutes[0] = EMPTYSTRING;

                }
                if(seconds[0] < 10){
                    oneDigitSeconds[0] = "0";

                }else{
                    oneDigitSeconds[0] = EMPTYSTRING;

                }
                dailyQuestionButton.setText(oneDigitHours[0] + hours[0] + ":" + oneDigitMinutes[0] + minutes[0] + ":" + oneDigitSeconds[0] + seconds[0]);

            }

            @Override
            public void onFinish() {
                dailyQuestionButton.setText(dailyQuestionButtonTextString);
                dailyQuestionButton.setEnabled(true);

            }

        }.start();

    }

    private void updateDailyQuestionTime(){
        date = new Date();
        LoggedUserData.loggedUserDailyQuestionTime = date.getTime();
        HashMap<String, Object> map = populateMap();
        FirebaseHelper.userDatabaseReference.child(LoggedUserData.loggedUserKey).setValue(map);

    }

    private void setDailyQuestionButton(){
        if(date.getTime() - LoggedUserData.loggedUserDailyQuestionTime < 60000){
            dailyQuestionButton.setEnabled(false);

            timer(60000 - (date.getTime() - LoggedUserData.loggedUserDailyQuestionTime));

        }else{
            dailyQuestionButton.setText(dailyQuestionButtonTextString);
            dailyQuestionButton.setEnabled(true);

        }

    }

}