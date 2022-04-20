package com.example.multimedia;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class HighscoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView myTextView;

    HighscoreViewHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.highscoreItem);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
