package com.example.triviaapp.game.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;
import com.example.triviaapp.game.GameSettingsActivity;
import com.example.triviaapp.game.HelpActivity;
import com.example.triviaapp.game.PlayActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.example.triviaapp.LoggedUserData.MIC;
import static com.example.triviaapp.LoggedUserData.optionList;

public class NotificationsFragment extends Fragment {
    Button startButton, exitButton,helpButton;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchMicrophone;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        initializeViews(root);
        setOnClickListeners();
        return root;

    }

    private void initializeViews(View root){
        startButton = root.findViewById(R.id.startBtn);
        exitButton = root.findViewById(R.id.exitBtn);
        switchMicrophone = root.findViewById(R.id.sw_microphone);
        switchMicrophone.setChecked(optionList.get(MIC).isValue());
        helpButton = root.findViewById(R.id.helpBtn);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        switchMicrophone.setText(R.string.microphoneSwitchGameMenuPlayEn);
        helpButton.setText(R.string.helpButtonGameMenuEn);

    }


    private void setViewForRomanianLanguage(){
        switchMicrophone.setText(R.string.microphoneSwitchGameMenuPlayRou);
        helpButton.setText(R.string.helpButtonGameMenuRou);

    }

    private void chooseLanguage(){
        switch (LoggedUserData.language.getValue()){
            case "english":
                setViewForEnglishLanguage();
                break;
            case "romanian":
                setViewForRomanianLanguage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + LoggedUserData.language.getValue());
        }

    }

    private void setOnClickListeners(){
        startButton.setOnClickListener((v) -> {openGameSettingsActivity();});
        exitButton.setOnClickListener((v) -> {exit();});
        switchMicrophone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            optionList.get(MIC).setValue(isChecked);
            editor.putString(optionList.get(MIC).getName(),String.valueOf(isChecked));
            editor.apply();

        });
        helpButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HelpActivity.class);
            startActivity(intent);
        });

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

}