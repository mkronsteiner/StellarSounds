package com.example.multimedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * GameOverActivity holds the Game Win Screen and displays points earned
 */

public class GameWinActivity extends AppCompatActivity {

    int points;

    SharedPreferences scores;
    SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "ScoresFile";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;

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

        //Load the highscores list and check if points are a new highscore
        ArrayList<Integer> testData = new ArrayList<>();

        scores = getSharedPreferences(PREFS_NAME, PREFS_MODE);
        editor = scores.edit();
        int defaultValue = 0;

        Map<String, ?> map = scores.getAll();
        for (int i = 1; i <= map.size(); i++) {
            int highScore = scores.getInt("score" + i, defaultValue);
            testData.add(highScore);
            //Log.d("HighscoreAdapter", "score:" + highScore);
        }

        Collections.sort(testData,  Collections.reverseOrder());

        TextView hsview = (TextView) findViewById(R.id.newHighscoreTextView);
        if (testData.get(0) == null || points >= testData.get(0)) hsview.setText("new \n highscore!");
        else hsview.setText("");

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