package com.example.triviaapp.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.triviaapp.R;

import static com.example.triviaapp.LoggedUserData.*;

public class GameSettingsActivity extends AppCompatActivity {
    private Switch sportCategorySwitch;
    private Switch geographyCategorySwitch;
    private Switch mathsCategorySwitch;
    private Switch othersCategorySwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
        initializeViews();
        switchesListeners();

    }

    private void initializeViews(){
        sportCategorySwitch = findViewById(R.id.sportCategorySwitch);
        geographyCategorySwitch = findViewById(R.id.geographyCategorySwitch);
        mathsCategorySwitch = findViewById(R.id.mathsCategorySwitch);
        othersCategorySwitch = findViewById(R.id.othersCategorySwitch);
        sportCategorySwitch.setChecked(optionList.get(SPORT).isValue());
        geographyCategorySwitch.setChecked(optionList.get(GEO).isValue());
        mathsCategorySwitch.setChecked(optionList.get(MATHS).isValue());
        othersCategorySwitch.setChecked(optionList.get(OTHERS).isValue());

    }

    private void switchListenerTemplate(int option, boolean value){
        SharedPreferences.Editor editor = getSharedPreferences("preferences.txt", MODE_PRIVATE).edit();
        optionList.get(option).setValue(value);
        editor.putString(optionList.get(option).getName(),String.valueOf(value));
        editor.apply();

    }

    private void switchesListeners(){
        sportCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(SPORT, isChecked);

        });

        geographyCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(GEO, isChecked);

        });

        mathsCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(MATHS, isChecked);

        });

        othersCategorySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchListenerTemplate(OTHERS, isChecked);

        });

    }

    public void openPlayActivity(View view){
        if(!optionList.get(SPORT).isValue() && !optionList.get(GEO).isValue() && !optionList.get(MATHS).isValue() && !optionList.get(OTHERS).isValue()){
            Toast.makeText(getBaseContext(),"Please select at least one category!",Toast.LENGTH_SHORT).show();
            return;

        }

        Intent intent = new Intent(getBaseContext(), PlayActivity.class);
        startActivity(intent);
        finishAndRemoveTask();

    }

}