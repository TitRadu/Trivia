package com.example.triviaapp.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.triviaapp.R;

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