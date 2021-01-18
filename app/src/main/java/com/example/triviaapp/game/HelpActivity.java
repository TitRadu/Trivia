package com.example.triviaapp.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.triviaapp.LoggedUserData;
import com.example.triviaapp.R;

public class HelpActivity extends AppCompatActivity {

    private Button btnexit;
    private TextView howToPlayTextView, howToPlayExplicationTextView, howToScoreTextView, howToScoreExplicationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setViews();
        btnexit.setOnClickListener(v -> {
            finishAndRemoveTask();
        });

    }

    private void setViews(){
        howToPlayTextView = findViewById(R.id.howToPlayTextView);
        howToPlayExplicationTextView = findViewById(R.id.howToPlayExplicationTextView);
        howToScoreTextView = findViewById(R.id.howToScoreTextView);
        howToScoreExplicationTextView = findViewById(R.id.howToScoreExplicationTextView);
        btnexit = findViewById(R.id.btn_exitHelp);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        howToPlayTextView.setText(R.string.howToPlayTextViewHelpEn);
        howToPlayExplicationTextView.setText(R.string.helpTextCumSeJoacaEn);
        howToScoreTextView.setText(R.string.howToScoreTextViewHelpEn);
        howToScoreExplicationTextView.setText(R.string.helpTextCumSePuncteazaEn);
        btnexit.setText(R.string.backButtonEditEn);

    }


    private void setViewForRomanianLanguage(){
        howToPlayTextView.setText(R.string.howToPlayTextViewHelpRou);
        howToPlayExplicationTextView.setText(R.string.helpTextCumSeJoacaRou);
        howToScoreTextView.setText(R.string.howToScoreTextViewHelpRou);
        howToScoreExplicationTextView.setText(R.string.helpTextCumSePuncteazaRou);
        btnexit.setText(R.string.backButtonEditRou);

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

}