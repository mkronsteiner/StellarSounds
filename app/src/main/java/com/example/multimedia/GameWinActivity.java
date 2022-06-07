package com.example.multimedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * GameOverActivity holds the Game Win Screen and displays points earned
 */

public class GameWinActivity extends AppCompatActivity {

    int points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GameWinActivity", "created");

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_win);

        points = 0;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            points = extras.getInt("points");
        }

        TextView view = (TextView) findViewById(R.id.pointsTextView);
        view.setText(points + " Points collected");

        final Button playAgainButton = (Button) findViewById(R.id.tryAgainButtonWin);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.tryAgainButtonWin){
                    Intent i = new Intent(GameWinActivity.this, GameActivity.class);
                    startActivity(i);
                }
            }

        });

        final Button returnButton = (Button) findViewById(R.id.returnButtonWin);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.returnButtonWin){
                    Log.d("GameOverActivity", "return clicked");
                    Intent i = new Intent(GameWinActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }

        });
    }

}