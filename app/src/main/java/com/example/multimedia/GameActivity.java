package com.example.multimedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
GameActivity holds the GameSurfaceView and Gameloop
 @author Mirjam Kronsteiner
 */

public class GameActivity extends AppCompatActivity {

    private GameSurfaceView gameView;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getSupportActionBar().hide();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameSurfaceView(this);
        setContentView(gameView);

        SharedPreferences scores = getSharedPreferences(PREFS_NAME, PREFS_MODE);

        SharedPreferences.Editor editor = scores.edit();

        editor.putInt("score1", 100);
        editor.putInt("score2", 150);
        editor.commit();
    }

}
