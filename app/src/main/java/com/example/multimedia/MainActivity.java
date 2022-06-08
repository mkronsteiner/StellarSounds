package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


/**
 MainActivity is the entryway to our application. It holds the start menu activity.
 @author Mirjam Kronsteiner
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getSupportActionBar().hide();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        final Button playButton = (Button) findViewById(R.id.playbutton);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.playbutton){
                    Intent i = new Intent(MainActivity.this, TutorialScreen.class);
                    startActivity(i);
                }
            }

        });

        final Button highscoresButton = (Button) findViewById(R.id.highscoresButton);
        highscoresButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.highscoresButton){
                    Intent i = new Intent(MainActivity.this, HighscoreActivity.class);
                    startActivity(i);
                }
            }

        });

        final Button levelsButton = (Button) findViewById(R.id.levelsButton);
        levelsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.levelsButton){
                    Intent i = new Intent(MainActivity.this, LevelSelectActivity.class);
                    startActivity(i);
                }
            }

        });
    }
}