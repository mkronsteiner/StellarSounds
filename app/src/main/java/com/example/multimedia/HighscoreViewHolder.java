package com.example.multimedia;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 HighscoreViewHolder holds an individual element of the RecyclerView (implemented in layout file highscore_item.xml)
 @author Mirjam Kronsteiner
 */

public class HighscoreViewHolder extends RecyclerView.ViewHolder{
    TextView myTextView;

    HighscoreViewHolder(View itemView) {
        super(itemView);
        myTextView = itemView.findViewById(R.id.highscoreItem);

    }
}
