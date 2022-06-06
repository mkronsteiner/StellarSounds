package com.example.multimedia;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Handles access to the background music, so it can be accessed over different activities.
 */

public class MusicPlayer {

    public static MediaPlayer mediaPlayer;
    public static boolean isPlaying;

    public static void playAudio(Context c, int id){
        mediaPlayer = MediaPlayer.create(c,id);
        isPlaying = false;

        if(!mediaPlayer.isPlaying())
        {
            isPlaying=true;
            mediaPlayer.start();
        }
    }

    public static void stopAudio(){
        isPlaying=false;
        mediaPlayer.stop();
    }
}
