package com.example.multimedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 HighscoreActivity contains the highscores which are displayed using a RecyclerView
 @author Mirjam Kronsteiner
 */


public class HighscoreActivity extends AppCompatActivity {

    HighscoreAdapter adapter;

    SharedPreferences scores;
    SharedPreferences.Editor editor;

    public static final String PREFS_NAME = "ScoresFile";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getSupportActionBar().hide();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_highscore);

        loadPrefs();



        final Button button = (Button) findViewById(R.id.clearScoresButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.clearScoresButton){
                    editor.clear();
                    editor.apply();
                    loadPrefs();
                }
            }

        });

    }

    private void loadPrefs() {
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

        RecyclerView recyclerView = findViewById(R.id.highscoreRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HighscoreAdapter(this, testData);
        recyclerView.setAdapter(adapter);
    }




}
