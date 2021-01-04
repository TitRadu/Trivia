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
import com.example.triviaapp.game.PlayActivity;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment {
    Button startButton, exitButton;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchMicropohone;

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
        switchMicropohone = root.findViewById(R.id.sw_microphone);
        switchMicropohone.setChecked(LoggedUserData.userMicrophone);

    }

    private void setOnClickListeners(){
        startButton.setOnClickListener((v) -> {openPlayActivity();});
        exitButton.setOnClickListener((v) -> {exit();});
        switchMicropohone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
            LoggedUserData.userMicrophone = isChecked;
            editor.putString("mic",String.valueOf(isChecked));
            editor.apply();
        });
    }

    public void openPlayActivity(){
        Intent intent = new Intent(getContext(), PlayActivity.class);
        startActivity(intent);
        getActivity().finishAndRemoveTask();

    }

    private void exit(){
        LoggedUserData.loggedUserPasswordUpdateVerify = false;
        getActivity().finishAndRemoveTask();

    }

}