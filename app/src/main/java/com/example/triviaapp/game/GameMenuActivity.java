package com.example.triviaapp.game;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;

import com.example.triviaapp.R;

public class GameMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clicked(View view){
        switch(view.getId()){
            case R.id.exitBtn:
                finishAndRemoveTask();
                break;
            case R.id.startBtn:
                openStartActivity();
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openStartActivity(){
        Intent intent = new Intent(this,GameMenuActivity.class);
        startActivity(intent);

    }

}