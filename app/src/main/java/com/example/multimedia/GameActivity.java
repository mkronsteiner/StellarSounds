package com.example.multimedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
GameActivity holds the GameSurfaceView and Gameloop
 @author Mirjam Kronsteiner
 */

public class GameActivity extends AppCompatActivity {

    private GameSurfaceView gameView;
    private int levelSelect;
    //MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MediaPlayer mp = MediaPlayer.create(getApplicationContext(),R.raw.track1);
        //mp.start();
        //getSupportActionBar().hide();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        levelSelect = 1;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            levelSelect = extras.getInt("level");
        }

        gameView = new GameSurfaceView(this, levelSelect);
        setContentView(gameView);

    }
}
