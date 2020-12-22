package com.example.triviaapp.game.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;
import com.example.triviaapp.game.PlayActivity;

public class NotificationsFragment extends Fragment {
    Button startButton, exitButton, microphoneButton;

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
        microphoneButton = root.findViewById(R.id.microphoneBtn);
        if(LoggedUserConstants.userMicrophone){
            microphoneButton.setText("Turn off microphone");

        }else{
            microphoneButton.setText("Turn on microphone");


        }

    }

    private void setOnClickListeners(){
        startButton.setOnClickListener((v) -> {openPlayActivity();});
        exitButton.setOnClickListener((v) -> {exit();});
        microphoneButton.setOnClickListener((v) -> {microphoneStatus();});

    }

    private void microphoneStatus(){
        if(LoggedUserConstants.userMicrophone){
            LoggedUserConstants.userMicrophone = false;
            microphoneButton.setText("Turn on microphone");

        }else{
            LoggedUserConstants.userMicrophone = true;
            microphoneButton.setText("Turn off microphone");


        }

    }

    public void openPlayActivity(){
        Intent intent = new Intent(getContext(), PlayActivity.class);
        startActivity(intent);

    }

    private void exit(){
        LoggedUserConstants.loggedUserPasswordUpdateVerify = false;
        getActivity().finishAndRemoveTask();

    }

}