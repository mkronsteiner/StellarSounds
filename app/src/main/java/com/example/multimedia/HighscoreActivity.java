package com.example.multimedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Array;
import java.util.ArrayList;

public class HighscoreActivity extends AppCompatActivity {

    HighscoreAdapter adapter;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getSupportActionBar().hide();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_highscore);

        ArrayList<Integer> testData = new ArrayList<>();
        testData.add(100);
        testData.add(200);
        testData.add(300);

        RecyclerView recyclerView = findViewById(R.id.highscoreRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HighscoreAdapter(this, testData);
        recyclerView.setAdapter(adapter);

        SharedPreferences scores = getSharedPreferences(PREFS_NAME, PREFS_MODE);

        int score1 = scores.getInt("score1", 100);

    }
}
