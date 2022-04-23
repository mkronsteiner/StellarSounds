package com.example.multimedia;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HighscoreViewHolder extends RecyclerView.ViewHolder{
    TextView myTextView;

    HighscoreViewHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.highscoreItem);

    }
}
