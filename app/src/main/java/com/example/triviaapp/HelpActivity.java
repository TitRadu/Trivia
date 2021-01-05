package com.example.triviaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.triviaapp.game.ui.notifications.NotificationsFragment;

public class HelpActivity extends AppCompatActivity {
    private Button btnexit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        btnexit = findViewById(R.id.btn_exitHelp);
        btnexit.setOnClickListener(v -> {
            finishAndRemoveTask();
        });
    }

}