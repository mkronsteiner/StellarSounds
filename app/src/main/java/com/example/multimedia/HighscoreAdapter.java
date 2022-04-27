package com.example.multimedia;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 HighscoreAdapter is a RecyclerView Adapter that holds the individual Highscore elements (implemented in HighscoreViewHolder)
 @author Mirjam Kronsteiner
 */

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreViewHolder> {

    private List<Integer> data;
    private LayoutInflater mInflater;

    public HighscoreAdapter(Context context, List<Integer> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public HighscoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.highscore_item, parent, false);
        return new HighscoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HighscoreViewHolder holder, int i) {

        holder.myTextView.setText(data.get(i).toString());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}




