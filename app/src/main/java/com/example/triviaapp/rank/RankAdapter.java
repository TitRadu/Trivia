package com.example.triviaapp.rank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviaapp.LoggedUserConstants;
import com.example.triviaapp.R;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankViewHolder>{
    private List<User> rankList;
    private Context context;

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.rank_row_list, parent, false);
        RankViewHolder viewHolder = new RankViewHolder(contactView);
        return viewHolder;

    }

    public RankAdapter(List<User> rankList){
        this.rankList = rankList;

    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        final User glassModel = rankList.get(position);
        holder.setValues(position + 1,glassModel.getUserName(),glassModel.getPoints());
        holder.itemView.setOnClickListener(v -> {});

    }

    @Override
    public int getItemCount() {
        return rankList.size();

    }

}
