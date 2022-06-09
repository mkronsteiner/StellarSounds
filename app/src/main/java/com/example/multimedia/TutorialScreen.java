package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class TutorialScreen extends AppCompatActivity {

    private boolean skipping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        skipping = false;

        setContentView(R.layout.activity_tutorial_screen);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView image = (ImageView) findViewById(R.id.tutorialScreenImage);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.tutorialscreen_animation);
        animation.setDuration(500);
        image.startAnimation(animation);

        //check language and change image if de
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (conf.locale.getLanguage().equals("de")) image.setImageResource(R.drawable.help_de);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!skipping) {
                    Intent i = new Intent(TutorialScreen.this, GameActivity.class);
                    i.putExtra("level", 1);
                    startActivity(i);
                }
            }
        }, 5000);
    }

    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("IntroVideoActivity", "skipping tutorial splash screen");
            skipping = true;
            Intent i = new Intent(TutorialScreen.this, GameActivity.class);
            i.putExtra("level", 1);
            startActivity(i);
        }
        return true;
    }
}