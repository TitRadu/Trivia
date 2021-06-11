package com.example.triviaapp.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.triviaapp.data.LoggedUserData;
import com.example.triviaapp.R;

public class HelpFragment extends Fragment {
    private TextView howToPlayTextView, howToPlayExplicationTextView, howToScoreTextView, howToScoreExplicationTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_help,container,false);
        setViews(root);
        return root;
    }

    private void setViews(View root){
        howToPlayTextView = root.findViewById(R.id.howToPlayTextView);
        howToPlayExplicationTextView = root.findViewById(R.id.howToPlayExplicationTextView);
        howToScoreTextView = root.findViewById(R.id.howToScoreTextView);
        howToScoreExplicationTextView = root.findViewById(R.id.howToScoreExplicationTextView);
        chooseLanguage();

    }

    private void setViewForEnglishLanguage(){
        howToPlayTextView.setText(R.string.howToPlayTextViewHelpEn);
        howToPlayExplicationTextView.setText(R.string.helpTextHowToPlayEn);
        howToScoreTextView.setText(R.string.howToScoreTextViewHelpEn);
        howToScoreExplicationTextView.setText(R.string.helpTextHowToScoreEn);

    }


    private void setViewForRomanianLanguage(){
        howToPlayTextView.setText(R.string.howToPlayTextViewHelpRou);
        howToPlayExplicationTextView.setText(R.string.helpTextHowToPlayRou);
        howToScoreTextView.setText(R.string.howToScoreTextViewHelpRou);
        howToScoreExplicationTextView.setText(R.string.helpTextHowToScoreRou);

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


