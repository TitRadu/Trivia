package com.example.triviaapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();
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

    public void setViews(){

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openStartActivity(){
        Intent intent = new Intent(this,StartActivity.class);
        startActivity(intent);

    }

}