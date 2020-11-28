package com.example.triviaapp.rank;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaapp.R;

public class RankViewHolder extends RecyclerView.ViewHolder{
    private TextView place;
    private TextView userName;
    private TextView points;

    public RankViewHolder(@NonNull View itemView) {
        super(itemView);
        initializeViews();
    }

    public void initializeViews(){
        place = itemView.findViewById(R.id.place);
        userName = itemView.findViewById(R.id.user_name);
        points = itemView.findViewById(R.id.points);

    }

    public void setValues(int placeP, String userNameP, int pointsP){
        place.setText(String.valueOf(placeP));
        userName.setText(userNameP);
        points.setText(String.valueOf(pointsP));

    }

}


