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
import com.example.triviaapp.game.GameActivity;
import com.example.triviaapp.game.GameSettingsActivity;
import com.example.triviaapp.game.LuckPlayModeActivity;
import com.example.triviaapp.game.PlayActivity;

import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.example.triviaapp.LoggedUserData.EMPTYSTRING;
import static com.example.triviaapp.LoggedUserData.MIC;
import static com.example.triviaapp.LoggedUserData.SPEAKER;
import static com.example.triviaapp.LoggedUserData.optionList;

public class NotificationsFragment extends Fragment {
    Button clasicButton, exitButton, dailyQuestionButton, luckModeButton;
    Date date;

    String dailyQuestionButtonTextString;
    String luckModeButtonTextString;


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchMicrophone, switchSpeaker;

    GameActivity gameActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        initializeViews(root);
        setOnClickListeners();
        setDailyQuestionButton();
        setLuckModeButton();
        return root;

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


    private void initializeViews(View root) {
        gameActivity = (GameActivity)getActivity();
        date = new Date();
        clasicButton = root.findViewById(R.id.classicBtn);
        exitButton = root.findViewById(R.id.exitBtn);
        switchMicrophone = root.findViewById(R.id.sw_microphone);
        switchMicrophone.setChecked(optionList.get(MIC).isValue());
        switchSpeaker = root.findViewById(R.id.sw_speaker);
        switchSpeaker.setChecked(optionList.get(SPEAKER).isValue());
        dailyQuestionButton = root.findViewById(R.id.dailyQuestionButton);
        luckModeButton = root.findViewById(R.id.luckModeButton);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage() {
        clasicButton.setText(R.string.classicButtonMenuEn);
        switchMicrophone.setText(R.string.microphoneSwitchMenuPlayEn);
        dailyQuestionButtonTextString = getString(R.string.dailyQuestionButtonMenuEn);
        luckModeButtonTextString = getString(R.string.luckModeButtonMenuEn);
        exitButton.setText(R.string.exitButtonMenuHelpEn);

    }


    private void setViewForRomanianLanguage() {
        clasicButton.setText(R.string.classicButtonMenuRou);
        switchMicrophone.setText(R.string.microphoneSwitchMenuPlayRou);
        dailyQuestionButtonTextString = getString(R.string.dailyQuestionButtonMenuRou);
        luckModeButtonTextString = getString(R.string.luckModeButtonMenuRou);
        exitButton.setText(R.string.exitButtonMenuHelpRou);

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

    private void setOnClickListeners() {
        clasicButton.setOnClickListener((v) -> {
            gameActivity.openGameSettingsActivity();

        });
        exitButton.setOnClickListener((v) -> {
            exit();

        });
        switchMicrophone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(MIC).setValue(isChecked);
            editor.putString(optionList.get(MIC).getName(), String.valueOf(isChecked));
            editor.apply();

        });
        switchSpeaker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(SPEAKER).setValue(isChecked);
            editor.putString(optionList.get(SPEAKER).getName(), String.valueOf(isChecked));
            editor.apply();

        });
        dailyQuestionButton.setOnClickListener((v) -> gameActivity.dailyQuestionPopUp());
        luckModeButton.setOnClickListener((v) -> gameActivity.luckModeActivity());

    }

    private void exit() {
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        getActivity().finishAndRemoveTask();

    }



    private void timer(long miliseconds, String control) {
        final long[] hours = {300000 / 3600000};
        final String[] oneDigitHours = {""};
        final long[] minutes = {(300000 / 60000) % (60)};
        final String[] oneDigitMinutes = {""};
        final long[] seconds = {(300000 / 1000) % 60};
        final String[] oneDigitSeconds = {""};
        new CountDownTimer(miliseconds, 1) {

            @Override
            public void onTick(long millisUntilFinished) {
                hours[0] = (millisUntilFinished / 3600000);
                minutes[0] = (millisUntilFinished / 60000) % 60;
                seconds[0] = (millisUntilFinished / 1000) % 60;
                if (hours[0] < 10) {
                    oneDigitHours[0] = "0";

                } else {
                    oneDigitHours[0] = EMPTYSTRING;

                }
                if (minutes[0] < 10) {
                    oneDigitMinutes[0] = "0";

                } else {
                    oneDigitMinutes[0] = EMPTYSTRING;

                }
                if (seconds[0] < 10) {
                    oneDigitSeconds[0] = "0";

                } else {
                    oneDigitSeconds[0] = EMPTYSTRING;

                }
                switch (control) {
                    case "DailyQuestion":
                        dailyQuestionButton.setText(oneDigitHours[0] + hours[0] + ":" + oneDigitMinutes[0] + minutes[0] + ":" + oneDigitSeconds[0] + seconds[0]);
                        break;
                    case "LuckMode":
                        luckModeButton.setText(oneDigitHours[0] + hours[0] + ":" + oneDigitMinutes[0] + minutes[0] + ":" + oneDigitSeconds[0] + seconds[0]);
                        break;
                }

            }

            @Override
            public void onFinish() {
                switch (control) {
                    case "DailyQuestion":
                        dailyQuestionButton.setText(dailyQuestionButtonTextString);
                        dailyQuestionButton.setEnabled(true);
                        break;
                    case "LuckMode":
                        luckModeButton.setText(luckModeButtonTextString);
                        luckModeButton.setEnabled(true);
                        break;

                }
            }

        }.start();

    }

    private void setDailyQuestionButton() {
        if (date.getTime() - LoggedUserData.loggedUserDailyQuestionTime < 60000) {
            dailyQuestionButton.setEnabled(false);

            timer(60000 - (date.getTime() - LoggedUserData.loggedUserDailyQuestionTime),"DailyQuestion");

        } else {
            dailyQuestionButton.setText(dailyQuestionButtonTextString);
            dailyQuestionButton.setEnabled(true);

        }

    }

    private void setLuckModeButton() {
        if (date.getTime() - LoggedUserData.loggedUserLuckModeTime < 60000) {
            luckModeButton.setEnabled(false);

            timer(60000 - (date.getTime() - LoggedUserData.loggedUserLuckModeTime),"LuckMode");

        } else {
            luckModeButton.setText(luckModeButtonTextString);
            luckModeButton.setEnabled(true);

        }

    }

}