package com.example.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

/**
 * SettingsActivity handles general settings that can be applied to the whole app
 */

public class SettingsActivity extends AppCompatActivity {

    private Resources res;

    static private boolean showFPS = false; //render fps count

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button returnButton = (Button) findViewById(R.id.returnButtonSettings);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.returnButtonSettings){
                    Log.d("GameOverActivity", "return clicked");
                    Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(i);
                }
            }

        });

        res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();

        //change language to german
        final ImageView imageAT = (ImageView) findViewById(R.id.imageAT);
        imageAT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.imageAT){
                    conf.locale = new Locale("de", "AT");
                    res.updateConfiguration(conf, dm);
                    updateStringLanguage();
                }
            }

        });

        //change language to english
        final ImageView imageGB = (ImageView) findViewById(R.id.imageGB);
        imageGB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.imageGB){
                    conf.locale = new Locale("gb");
                    res.updateConfiguration(conf, dm);
                    updateStringLanguage();
                }
            }

        });

        //If FPS button is clicked change showFPS and update text
        final TextView fpsSwitch = findViewById(R.id.fpsSwitchText);
        fpsSwitch.setText(res.getString(getOnOff()));
        fpsSwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v.getId()==R.id.fpsSwitchText){
                    showFPS = !showFPS;
                    fpsSwitch.setText(res.getString(getOnOff()));
                }
            }

        });
    }

    /**
     * @return the id of the "on" or "off" string based on showFPS value
     */
    private int getOnOff() {
        if (showFPS) return R.string.on;
        else return R.string.off;
    }

    /**
     * update all the strings in this activity when the language is changed
     */
    private void updateStringLanguage() {
        TextView languageText = findViewById(R.id.textLanguage);
        languageText.setText(R.string.language);
        TextView fpsText = findViewById(R.id.textFPS);
        fpsText.setText(R.string.showfps);
        TextView fpsSwitch = findViewById(R.id.fpsSwitchText);
        fpsSwitch.setText(res.getString(getOnOff()));
        Button returnText = findViewById(R.id.returnButtonSettings);
        returnText.setText(R.string.returnB);
    }

    static public boolean getShowFPS() {
        return showFPS;
    }


}