package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * GameOverActivity holds the Game Over Screen
 */

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GameOverActivity", "created");

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_over);

        final Button playAgainButton = (Button) findViewById(R.id.tryAgainButton);
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.tryAgainButton){
                    Log.d("GameOverActivity", "try again clicked");
                    Intent i = new Intent(GameOverActivity.this, GameActivity.class);
                    startActivity(i);
                }
            }

        });

        final Button returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.returnButton){
                    Log.d("GameOverActivity", "return clicked");
                    Intent i = new Intent(GameOverActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }

        });
    }
}