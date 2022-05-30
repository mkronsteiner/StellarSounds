package com.example.multimedia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_level_select);

        final Button level1Button = (Button) findViewById(R.id.buttonLevel1);
        level1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.buttonLevel1){
                    Log.d("GameOverActivity", "try again clicked");
                    Intent i = new Intent(LevelSelectActivity.this, GameActivity.class);
                    i.putExtra("level", 1);
                    startActivity(i);
                }
            }

        });

        final Button level2Button = (Button) findViewById(R.id.buttonLevel2);
        level2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.buttonLevel2){
                    Log.d("GameOverActivity", "try again clicked");
                    Intent i = new Intent(LevelSelectActivity.this, GameActivity.class);
                    i.putExtra("level", 2);
                    startActivity(i);
                }
            }

        });

        final Button level3Button = (Button) findViewById(R.id.buttonLevel3);
        level3Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.buttonLevel3){
                    Log.d("GameOverActivity", "try again clicked");
                    Intent i = new Intent(LevelSelectActivity.this, GameActivity.class);
                    i.putExtra("level", 3);
                    startActivity(i);
                }
            }

        });
    }
}
