package com.example.multimedia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 GameSurfaceView holds all objects of the game, implements update and render methods
 @author Mirjam Kronsteiner
 */

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Random rand;

    private Context context;
    private Resources res;
    private SurfaceHolder holder;
    private GameLoop gameLoop;

    //LEVEL SELECT
    private int levelSelect;
    private double[] data1, data2, data3;
    private LevelMap map, map1, map2, map3;

    //POSITIONS & MOVEMENT
    private double playerPos; //absolute position on screen
    private double relPlayerPos; //player pos as relative value on the x axis
    private double linePos;
    private double charaWidth;
    private double charaHeight;
    private boolean isTouching;
    private boolean paused, muted;

    //GAMEPLAY
    private int lives;
    private int points;
    private int invincibilityTime; //current time of invincibility left, in frames
    private int shieldPower; //seconds of invincibility given by shield
    private int shieldLock; //like invincibility time, prevents shield from being collected more than once

    //GRAPHICS
    private Bitmap rocket, rocket1, rocket2, rocket3, rocketFrame, rocketShield;
    private float playerX, playerY;
    private Bitmap background;
    private Bitmap asteroid, shield, star;
    private Bitmap pauseButton, playButton, bigPauseButton;
    private Bitmap soundOffButton, soundOnButton;
    final private Paint red, text, white;
    private boolean showFPS;

    //ANIMATION
    private int frameCount;
    private int currentFrame;
    private int frameWidth;
    private int frameHeight;
    private long lastFrameChangeTime;
    private int frameLength;
    private Rect animationFrame;
    private Rect frameLoc;

    //MUSIC
    private MediaPlayer mp;

    //HIGHSCORES
    public static final String PREFS_NAME = "ScoresFile";
    public static final int PREFS_MODE = Context.MODE_PRIVATE;
    SharedPreferences scores;
    SharedPreferences.Editor editor;

    public GameSurfaceView(Context context, int levelSelect) {
        super(context);

        // Callback für Events hinzufügen:
        getHolder().addCallback(this);

        this.context = context;

        holder = getHolder();
        gameLoop = new GameLoop(this, holder);

        // Damit Events behandelt werden:
        setFocusable(true);

        // z.B. Ressourcen initialisieren

        res = context.getResources();

        //Initialise and resize all Bitmaps

        int scale = 6;
        rocket1 = BitmapFactory.decodeResource(res, R.drawable.rocket_sheet1);
        rocket1 = getResizedBitmap(rocket1, rocket1.getWidth()/scale, rocket1.getHeight()/(scale));

        rocket2 = BitmapFactory.decodeResource(res, R.drawable.rocket_sheet2);
        rocket2 = getResizedBitmap(rocket2, rocket2.getWidth()/scale, rocket2.getHeight()/(scale));

        rocket3 = BitmapFactory.decodeResource(res, R.drawable.rocket_sheet3);
        rocket3 = getResizedBitmap(rocket3, rocket3.getWidth()/scale, rocket3.getHeight()/(scale));

        rocketShield = BitmapFactory.decodeResource(res, R.drawable.rocket_shield);
        rocketShield = getResizedBitmap(rocketShield, (rocket3.getWidth()/4), rocket3.getHeight());

        //Rocket Bitmap for Collision detection, with the flames cropped since they don't count for collision
        rocketFrame = BitmapFactory.decodeResource(res, R.drawable.rocket_first_stage_small);
        rocketFrame = getResizedBitmap(rocketFrame, rocket1.getWidth()/4, rocket1.getHeight());
        rocketFrame = Bitmap.createBitmap(rocketFrame, 0, 0, rocketFrame.getWidth(), rocketFrame.getHeight()/2 +100);

        rocket = rocket1;

        background = BitmapFactory.decodeResource(res, R.drawable.background);

        asteroid = BitmapFactory.decodeResource(res, R.drawable.meteor);
        asteroid = getResizedBitmap(asteroid, 150, 150);

        shield = BitmapFactory.decodeResource(res, R.drawable.shield_edit);
        shield = getResizedBitmap(shield, 150, 150);

        star = BitmapFactory.decodeResource(res, R.drawable.star);
        star = getResizedBitmap(star, 100, 100);

        pauseButton = BitmapFactory.decodeResource(res, R.drawable.pause_edit);
        pauseButton = getResizedBitmap(pauseButton, 100, 100);

        bigPauseButton = pauseButton.copy(pauseButton.getConfig(), true);
        bigPauseButton = getResizedBitmap(bigPauseButton, 300, 300);

        playButton = BitmapFactory.decodeResource(res, R.drawable.play_edit);
        playButton = getResizedBitmap(playButton, 100, 100);

        soundOffButton = BitmapFactory.decodeResource(res, R.drawable.soundoff_edit);
        soundOffButton = getResizedBitmap(soundOffButton, 100, 100);

        soundOnButton = BitmapFactory.decodeResource(res, R.drawable.soundon_edit);
        soundOnButton = getResizedBitmap(soundOnButton, 100, 100);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(6);

        text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(60);
        Typeface type = ResourcesCompat.getFont(context, R.font.aldrich);
        text.setTypeface(type);

        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(10);
        white.setStyle(Paint.Style.STROKE);

        lives = 3;
        points = 0;
        invincibilityTime = 0;
        shieldPower = 180;
        shieldLock = 0;

        this.levelSelect = levelSelect;

        mp = MediaPlayer.create(context,R.raw.track1);

        rand = new Random();
        showFPS = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        //stop all other music playing
        if (MusicPlayer.isPlaying) MusicPlayer.stopAudio();

        gameLoop.startLoop();
        paused = false;

        mp.start();
        muted = false;

        //init
        playerPos = getWidth() / 2;
        linePos = getHeight() - 600;
        isTouching = false;
        playerY = (float) linePos;

        frameCount = 4;
        currentFrame = 0;
        frameWidth = rocket1.getWidth()/frameCount;
        frameHeight = rocket1.getHeight();
        lastFrameChangeTime = 0;
        frameLength = 1000;
        animationFrame = new Rect(0, 0, frameWidth, frameHeight);

        playerX = (float) playerPos - frameWidth/2.0f;
        frameLoc = new Rect((int) playerX, (int) playerY, (int) playerX + frameWidth, (int) playerY + frameWidth);

        charaHeight = frameHeight;
        charaWidth = frameWidth;

        //choose level based on levelSelect parameter and init level (object of LevelMap)
        //maps are stores in data variables and initialized in initMapData()
        initMapData();

        double data[];
        map = new LevelMap(this);
        map.initPos();

        Log.d("LevelSelect", "Level: " + levelSelect);
        switch (levelSelect) {
            case 1: data = data1; break;
            case 2: data = data2; break;
            case 3: data = data3; break;
            default: data = data1; break;
        }
        map.loadMap(data);

        map.initBitmaps(asteroid, shield, star);
        background = getResizedBitmap(background, getWidth(), getHeight());

        showFPS = SettingsActivity.getShowFPS();

    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("GameSurfaceView", "surface destroyed");
        mp.stop();
        if (paused) gameLoop.resumeLoop();
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
            //Sound button
            } else if (startX > getRight()-soundOffButton.getWidth() -20 && startX < getRight() - 20 && startY > 120 && startY < 220) {
                if (!muted) {
                    mp.pause();
                    muted = true;
                } else {
                    mp.start();
                    muted = false;
                }
            //testing button
            } else if (startX > 20 && startX < 120 && startY > 320 && startY < 420) {

            } else { isTouching = false; }
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            endX = e.getX();

            if (isTouching) playerPos = endX;
        }
        return true;
    }

    /**
     * updates the values per frame (item times, points earned) and calls update() in LevelMap
     * checks if lives == 0
     */
    public void update() {

        if (invincibilityTime > 0) invincibilityTime--;
        if (shieldLock > 0) shieldLock--;



        map.update();
        updatePoints();

        if (lives == 0) {
            Log.d("update()", "lives <= 0");
            gameOver();

        }
        //Log.d("update", "cur:" + map.getCurrent());
    }

    /**
     * Implements Game Over state, starts the GameOverActivity
     */
    public void gameOver() {
        pauseGame();
        addScore(points);

        mp.stop();

        Intent i = new Intent(context, GameOverActivity.class);
        context.startActivity(i);
    }

    /**
     * Implements Game Win state, starts the GameWinActivity
     */
    public void gameWin() {
        pauseGame();
        addScore(points);

        mp.stop();

        Intent i = new Intent(context, GameWinActivity.class);
        i.putExtra("points", points);
        context.startActivity(i);
    }

    public void pauseGame() {
        paused = true;
        mp.pause();
        gameLoop.pauseLoop();
    }

    public void resumeGame() {
        paused = false;
        mp.start();
        gameLoop.resumeLoop();
    }

    /**
     * add a score to the shared preferences holding the highscore list
     * @param score the score to add
     */
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

        //point gain based on dif
        if (map.isTouching()) {
            dif = Math.abs(relPlayerPos - map.getCurrent());
            if (dif > 0.0 && dif < 0.015) {
                points += 3;
            } else if (dif < 0.035) {
                points += 2;
            } else if (dif < 0.055) {
                points += 1;
            }
            //Log.d("updatePoints()", "dif: " + dif);
        }

    }

    /**
     * called by LevelMap.draw() on collision with an asteroid
     * invincibilityTime is used as a countdown so asteroids are not counted twice in one collision
     */
    public void collideWithAsteroid() {
        if (invincibilityTime == 0) {
            lives--;
            updateRocketBitmap();
        } else {
            invincibilityTime--;
        }



    }

    /**
     * called by LevelMap.draw() on collision with a shield
     * shieldLock is used as a countdown so shields cannot be collected twice in one collision
     */
    public void collideWithShield() {
        if (shieldLock == 0) {
            invincibilityTime = shieldPower;
            shieldLock = 180;
        } else {
            shieldLock--;
        }
    }


    /**
     * draws rocket and ui elements and calls draw in LevelMap
     * @param c the canvas to draw on
     */
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
        playerX = (float) playerPos - frameWidth/2.0f;
        frameLoc.set((int) playerX, (int) playerY, (int) playerX + frameWidth, (int) playerY+frameHeight);

        switchAnimationFrame();
        c.drawBitmap(rocket, animationFrame, frameLoc, null);

        if (!paused) c.drawBitmap(pauseButton,20,120, null);

        if (!muted) c.drawBitmap(soundOffButton, getRight()-soundOffButton.getWidth()-20, 120, null);
        else c.drawBitmap(soundOnButton, getRight()-soundOnButton.getWidth()-20, 120, null);

        if (invincibilityTime > 0) {
            //c.drawCircle(playerX+frameWidth/2.0f, playerY+frameHeight/2.0f - 20, frameHeight/2.0f + 40, white);
            c.drawBitmap(rocketShield, (int) playerX-3, (int) playerY-12, null);
        }

        //text
        c.drawText(res.getString(R.string.lives) + ": " + lives, 30, 100, text);
        c.drawText(res.getString(R.string.points) + ": " + + points, getRight() / 2.0f +50, 100, text);
        if (showFPS) c.drawText("FPS:" + Math.round(gameLoop.getFps()*100)/100, 30, getBottom()-50, text);
        //c.drawRect(20, 320, 120, 420, red); //testing button

    }

    /**
     * calculate frame on the sprite sheet and replace animationFrame with the current frame
     */
    public void switchAnimationFrame(){

        long time  = System.currentTimeMillis();
        if ( time > lastFrameChangeTime + frameLength) {
            lastFrameChangeTime = time;
            currentFrame ++;
            if (currentFrame >= frameCount) {
                currentFrame = 0;
            }
        }
        //update the left and right values of the source of
        //the next frame on the spritesheet
        animationFrame.left = currentFrame * frameWidth;
        animationFrame.right = animationFrame.left + frameWidth;
    }

    /**
     * switch the rocket bitmap based on remaining lives
     */
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

    public void drawPauseScreen(Canvas c) {
        Log.d("GameSurfaceView", "drawing pause screen");

        c.drawBitmap(background, 0, 0, null);

        map.draw(c);

        playerX = (float) playerPos - frameWidth/2.0f;
        frameLoc.set((int) playerX, (int) playerY, (int) playerX + frameWidth, (int) playerY+frameHeight);
        c.drawBitmap(rocket, animationFrame, frameLoc, null);

        c.drawBitmap(playButton,20,120, null);

        if (invincibilityTime > 0) {

            c.drawBitmap(rocketShield, (int) playerX-3, (int) playerY-12, null);

            //Log.d("GameSurfaceView.draw()", "invincibility");
            //c.drawCircle(playerX+frameWidth/2.0f, playerY+frameHeight/2.0f - 20, frameHeight/2.0f + 40, white);
            //c.drawCircle(playerX+frameWidth/2.0f, playerY+frameHeight/2.0f - 20, frameHeight/2.0f + 40, whiteShield);
        }

        c.drawText(res.getString(R.string.lives) + ": " + lives, 30, 100, text);
        c.drawText(res.getString(R.string.points) + ": " + + points, getRight() / 2.0f +50, 100, text);
        if (showFPS) c.drawText("FPS:" + Math.round(gameLoop.getFps()*100)/100, 30, getBottom()-50, text);

        int margin = 100;
        //c.drawRect(new Rect(margin, margin, getWidth() - margin, getHeight() - margin), white);
        c.drawBitmap(bigPauseButton, getRight()/2.0f - bigPauseButton.getWidth()/2.0f, getBottom()/2.0f - bigPauseButton.getHeight()/2.0f, null);
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
        return rocketFrame;
    }

    public int getPlayerX() {
        return (int) playerPos;
    }

    public int getPlayerY() {
        return (int) playerY;
    }

    public void toggleShowFPS() {
        if (showFPS) showFPS = false;
        if (!showFPS) showFPS = true;
    }

    /**
     * Initialize the level data
     * Level values are stored in "data" arrays
     */
    private void initMapData() {
        //horizontal positions of map elements are assigned in the map array, vertical positions are stored in vertPos(and changed per frame)
        //0 = empty, 1 = point, 2 = asteroid, 3 = shield

        //positions on x axis
        data1 = new double[]
                {0.0, 1.5, 1.4, 1.2,
                0.0, 2.4, 0.0, 3.8,
                0.0, 0.0, 0.0, 0.0,
                1.5, 1.6, 1.2, 2.5,
                0.0, 0.0, 1.2, 1.8,
                0.0, 1.2, 1.9, 2.3,
                0.0, 0.0, 0.0, 0.0,
                1.6, 1.2, 1.8, 1.3,
                0.0, 0.0, 0.0, 2.4,
                0.0, 0.0, 0.0, 2.8,
                0.0, 0.0, 0.0, 2.5,
                0.0, 0.0, 0.0, 0.0};

        data2 = new double[]
                {3.5, 0.0, 1.1, 1.8,
                0.0, 0.0, 1.1, 1.8,
                0.0, 1.1, 1.8, 1.5,
                1.8, 0.0, 3.5, 0.0,
                1.2, 1.5, 2.5, 0.0,
                1.4, 1.2, 0.0, 1.9,
                1.4, 0.0, 1.9, 1.2,
                1.7, 1.3, 1.5, 1.4,
                2.4, 0.0, 0.0, 1.8,
                1.5, 1.9, 1.6, 2.6,
                0.0, 0.0, 1.3, 1.6,
                2.6, 0.0, 0.0, 1.3,
                1.6, 1.3, 0.0, 0,0};

        data3 = new double[]
                {0.0, 2.9, 0.0, 1.2,
                1.9, 1.4, 0.0, 1.2,
                1.9, 1.5, 1.7, 1.6,
                0.0, 2.7, 3.5, 2.5,
                0.0, 1.1, 1.4, 1.3,
                1.6, 0.0, 2.1, 3.7,
                0.0, 1.2, 1.9, 1.4,
                0.0, 1.2, 1.9, 1.4,
                0.0, 2.2, 2.1, 0.0,
                1.3, 1.6, 0.0, 2.9,
                1.6, 1.3, 0.0, 2.2,
                0.0, 0.0, 1.5, 1.2,
                1.6, 1.3, 0.0, 0.0};
    }
}

