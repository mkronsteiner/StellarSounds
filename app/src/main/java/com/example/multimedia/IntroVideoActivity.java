package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * IntroVideoActivity holds the Intro Video Player
 */
public class IntroVideoActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    MediaController mediaPlayer;
    VideoView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MusicPlayer.playAudio(this, R.raw.menu_music);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_video);

        view = (VideoView) findViewById(R.id.videoView);
        view.setMediaController(mediaPlayer);
        view.setOnCompletionListener(this);

        mediaPlayer = new MediaController(this);
        mediaPlayer.setAnchorView(view);

        String uriPath = "android.resource://" + getPackageName() + "/" + R.raw.testvideo;
        Uri uri = Uri.parse(uriPath);
        view.setVideoURI(uri);
        view.requestFocus();
        view.start();


    }

    public boolean onTouchEvent(MotionEvent e) {

        //skip the video
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("IntroVideoActivity", "skipping intro video");
            Intent i = new Intent(IntroVideoActivity.this, MainActivity.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("IntroVideoActivity", "video completed");
        Intent i = new Intent(IntroVideoActivity.this, MainActivity.class);
        startActivity(i);
    }
}