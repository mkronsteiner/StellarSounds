package com.example.multimedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 GameSurfaceView holds all objects of the game, implements update and render methods
 @author Mirjam Kronsteiner
 */

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder holder;
    private GameLoop gameLoop;
    private LevelMap map;

    //MediaPlayer mediaPlayer;


    private Random rand;

    //POSITIONS & MOVEMENT
    private double playerPos; //absolute position on screen
    private double relPlayerPos; //player pos as relative value on the x axis
    private double linePos;
    private double charaWidth;
    private double charaHeight;
    private boolean isTouching;
    private boolean paused;

    //GAMEPLAY
    private int lives;
    private int points;
    private int invincibilityTime; //current time of invincibility left, in frames
    private int shieldPower; //seconds of invincibility given by shield
    private int shieldLock; //like invincibility time, prevents shield from being collected more than once

    //GRAPHICS
    private Bitmap rocket, rocket1, rocket2, rocket3;
    private float playerX, playerY;
    private Bitmap background;
    private Bitmap asteroid, shield;
    private Bitmap pauseButton, playButton, bigPauseButton;
    final private Paint red, text, white;
    private Rect boundingBox;

    //HIGHSCORES
    public static final String PREFS_NAME = "ScoresFile";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;
    SharedPreferences scores;
    SharedPreferences.Editor editor;

    public GameSurfaceView(Context context) {
        super(context);

        // Callback für Events hinzufügen:
        getHolder().addCallback(this);

        this.context = context;

        holder = getHolder();
        gameLoop = new GameLoop(this, holder);

        // Damit Events behandelt werden:
        setFocusable(true);

        // z.B. Ressourcen initialisieren
        Resources res = context.getResources();

        rocket1 = BitmapFactory.decodeResource(res, R.drawable.rocket_first_stage_small);
        rocket1 = getResizedBitmap(rocket1, rocket1.getWidth()/5, rocket1.getHeight()/5);

        rocket2 = BitmapFactory.decodeResource(res, R.drawable.rocket_second_stage_small);
        rocket2 = getResizedBitmap(rocket2, rocket2.getWidth()/5, rocket2.getHeight()/5);


        rocket3 = BitmapFactory.decodeResource(res, R.drawable.rocket_third_stage_small);
        rocket3 = getResizedBitmap(rocket3, rocket3.getWidth()/5, rocket3.getHeight()/5);

        rocket = rocket1;

        background = BitmapFactory.decodeResource(res, R.drawable.background);

        asteroid = BitmapFactory.decodeResource(res, R.drawable.meteor);
        asteroid = getResizedBitmap(asteroid, 150, 150);

        shield = BitmapFactory.decodeResource(res, R.drawable.shield_edit);
        shield = getResizedBitmap(shield, 150, 150);

        pauseButton = BitmapFactory.decodeResource(res, R.drawable.pause_edit);
        pauseButton = getResizedBitmap(pauseButton, 100, 100);

        bigPauseButton = pauseButton.copy(pauseButton.getConfig(), true);
        bigPauseButton = getResizedBitmap(bigPauseButton, 300, 300);

        playButton = BitmapFactory.decodeResource(res, R.drawable.play_edit);
        playButton = getResizedBitmap(playButton, 100, 100);


        boundingBox = new Rect(0, 0, 0, 0);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(6);

        text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(80);

        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(10);
        white.setStyle(Paint.Style.STROKE);

        lives = 3;
        points = 0;
        invincibilityTime = 0;
        shieldPower = 180;
        shieldLock = 0;

        map = new LevelMap(this);

        rand = new Random();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


        gameLoop.startLoop();
        paused = false;

        //init
        playerPos = getWidth() / 2;
        linePos = getHeight() - 800;
        charaHeight = rocket1.getHeight();
        charaWidth = rocket1.getWidth();
        isTouching = false;
        playerX = 0;
        playerY = (float) linePos;
        map.initPos();
        map.loadMap();

        map.initBitmaps(asteroid, shield);
        background = getResizedBitmap(background, getWidth(), getHeight());



        //updateRocketBitmap();




    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("GameSurfaceView", "surface destroyed");
        gameLoop.stopLoop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float startX = e.getX();
        float startY = e.getY();
        float endX = 0;

    // Ein Touch-Event wurde ausgelöst
        if(e.getAction() == MotionEvent.ACTION_DOWN) {

        // Was soll bei einer Berührung passieren?

            //if the rocket is touched
            if (startX > playerPos - charaWidth && startX < playerPos + charaWidth &&
                    startY > linePos && startY < linePos + charaHeight) {
                if (!paused) isTouching = true;

            //Pause/Play Button
            } else if (startX > 20 && startX < 120 && startY > 120 && startY < 220) {
                if (!paused) pauseGame();
                else resumeGame();
            } else if (startX > 20 && startX < 120 && startY > 320 && startY < 420) {
                gameWin();
            } else { isTouching = false; }
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            endX = e.getX();

            if (isTouching) playerPos = endX;
        }
        return true;
    }

    public void update() {

        if (invincibilityTime > 0) invincibilityTime--;
        if (shieldLock > 0) shieldLock--;



        map.update();
        updatePoints();
        //Log.d("update", "cur:" + map.getCurrent());
    }

    public void gameOver() {
        addScore(points);

        Intent i = new Intent(context, GameOverActivity.class);
        context.startActivity(i);

        gameLoop.stopLoop();
    }

    public void gameWin() {
        addScore(points);

        Intent i = new Intent(context, GameWinActivity.class);
        i.putExtra("points", points);
        context.startActivity(i);

        gameLoop.stopLoop();
    }

    public void pauseGame() {
        paused = true;
        gameLoop.pauseLoop();
        //mediaPlayer.pause();
    }

    public void resumeGame() {
        paused = false;
        gameLoop.resumeLoop();
        //mediaPlayer.start();
    }

    public void addScore(int score) {
        scores = context.getSharedPreferences(PREFS_NAME, PREFS_MODE);
        editor = scores.edit();
        //editor.clear();
        Map<String, ?> map = scores.getAll();
        int nextVal = map.size() +1;

        editor.putInt("score" + nextVal, score);
        editor.commit();
    }

    //check how many points are earned each frame and update points count
    //points are earned based on the difference between player position and current map value
    private void updatePoints() {
        relPlayerPos = playerPos / getRight();
        relPlayerPos = Math.round(relPlayerPos * 100) / 100.0;
        //Log.d("updatePoints()", "rel:" + relPlayerPos);

        double dif; //difference between player position and current map value
        if (map.isTouching()) {
            dif = Math.abs(relPlayerPos - map.getCurrent());
        } else {
            dif = 0.0;
        }
        //Log.d("updatePoints()", "dif: " + dif);

        //point gain based on dif
        if (dif == 0.0) {

        } else if (dif > 0.0 && dif < 0.015) {
            points += 3;
        } else if (dif < 0.035) {
            points += 2;
        } else if (dif < 0.055) {
            points += 1;
        }

    }

    public void collideWithAsteroid() {
        if (invincibilityTime == 0) {
            lives--;
            updateRocketBitmap();
        } else {
            invincibilityTime--;
        }

        if (lives == 0) {
            Log.d("collideWithAsteroid", "lives <= 0");
            gameOver();

        }

    }

    public void collideWithShield() {
        if (shieldLock == 0) {
            invincibilityTime = shieldPower;
            shieldLock = 180;
        } else {
            shieldLock--;
        }
    }


    public void draw(Canvas c) {
        //Log.d("GameSurfaceView.java", "draw()");
        super.draw(c);

        //background
        c.drawBitmap(background, 0, 0, null);

        //Guide lines
        //c.drawLine(getLeft(), (float) linePos, getRight(), (float) linePos, red);
        //c.drawLine(getWidth()/2, getBottom(), getWidth()/2, getTop(), red);

        //map
        map.draw(c);

        //rocket

        playerX = (float) playerPos - rocket.getWidth()/2.0f;

        c.drawBitmap(rocket, playerX, playerY, null);

        if (!paused) c.drawBitmap(pauseButton,20,120, null);


        if (invincibilityTime > 0) {
            //Log.d("GameSurfaceView.draw()", "invincibility");
            c.drawCircle(playerX+150, playerY+300, 400, white);
        }

        //text
        c.drawText("Lives: " + lives, 30, 100, text);
        c.drawText("Points: " + points, getRight() / 2.0f, 100, text);
        c.drawText("FPS:" + Math.round(gameLoop.getFps()*100)/100, 30, getBottom()-50, text);
        c.drawRect(20, 320, 120, 420, red); //testing button

    }

    public void drawPauseScreen(Canvas c) {
        Log.d("GameSurfaceView", "drawing pause screen");

        c.drawBitmap(background, 0, 0, null);

        map.draw(c);

        playerX = (float) playerPos - rocket.getWidth()/2.0f;
        c.drawBitmap(rocket, playerX, playerY, null);

        c.drawBitmap(playButton,20,120, null);

        if (invincibilityTime > 0) {
            //Log.d("GameSurfaceView.draw()", "invincibility");
            c.drawCircle(playerX+150, playerY+300, 400, white);
        }

        c.drawText("Lives: " + lives, 30, 100, text);
        c.drawText("Points: " + points, getRight() / 2.0f, 100, text);
        c.drawText("FPS:" + Math.round(gameLoop.getFps()*100)/100, 30, getBottom()-50, text);

        int margin = 100;
        //c.drawRect(new Rect(margin, margin, getWidth() - margin, getHeight() - margin), white);
        c.drawBitmap(bigPauseButton, getRight()/2.0f - bigPauseButton.getWidth()/2.0f, getBottom()/2.0f - bigPauseButton.getHeight()/2.0f, null);
    }

    private void updateRocketBitmap() {

        switch (lives) {
            case 1:
                rocket = rocket3;
                break;
            case 2:
                rocket = rocket2;
                break;
            case 3:
                rocket = rocket1;
                break;
            default:
                rocket = rocket1;
                break;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public double getLinePos() {
        return linePos;
    }

    public Bitmap getPlayer() {
        return rocket3;
    }

    public int getPlayerX() {
        return (int) playerPos;
    }

    public int getPlayerY() {
        return (int) playerY;
    }

}

