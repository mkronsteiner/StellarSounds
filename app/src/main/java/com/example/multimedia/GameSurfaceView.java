package com.example.multimedia;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 GameSurfaceView holds all objects of the game, implements update and render methods
 @author Mirjam Kronsteiner
 */

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder holder;
    private GameLoop gameLoop;
    private LevelMap map;

    //POSITIONS & MOVEMENT
    private double playerPos;
    private double linePos;
    private double charaWidth;
    private double charaHeight;
    private boolean isTouching;

    //GAMEPLAY
    private int lives;
    private boolean testGO;

    //GRAPHICS
    private Bitmap rocket1, rocket2, rocket3;
    private Bitmap background;
    private Bitmap asteroid, shield;

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

        rocket1 = BitmapFactory.decodeResource(res, R.drawable.rocket_stage_1);
        rocket1 = getResizedBitmap(rocket1, rocket1.getWidth(), rocket1.getHeight()-70);

        rocket2 = BitmapFactory.decodeResource(res, R.drawable.rocket_stage_2);
        rocket2 = getResizedBitmap(rocket2, rocket2.getWidth(), rocket2.getHeight()-70);

        rocket3 = BitmapFactory.decodeResource(res, R.drawable.rocket_stage_3);
        rocket3 = getResizedBitmap(rocket3, rocket3.getWidth(), rocket3.getHeight()-70);

        background = BitmapFactory.decodeResource(res, R.drawable.background);

        asteroid = BitmapFactory.decodeResource(res, R.drawable.meteor);
        asteroid = getResizedBitmap(asteroid, asteroid.getWidth()/2, asteroid.getHeight()/2);

        shield = BitmapFactory.decodeResource(res, R.drawable.shield);
        shield = getResizedBitmap(shield, shield.getWidth()/2, shield.getHeight()/2);

        lives = 3;
        testGO = false;

        map = new LevelMap(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();

        //init
        playerPos = getWidth() / 2;
        linePos = getHeight() - 600;
        charaHeight = rocket1.getHeight();
        charaWidth = rocket1.getWidth();
        isTouching = false;

        map.initPos();
        map.loadMap();
        map.initBitmaps(asteroid, shield);

        background = getResizedBitmap(background, getWidth(), getHeight());
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
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
                    startY > linePos - charaHeight/2 && startY < linePos + charaHeight/2) {
                isTouching = true;

            //Testing button
            } else if (startX > 20 && startX < 120 && startY > 120 && startY < 220) {
                lives--;

            } else { isTouching = false; }
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            endX = e.getX();

            if (isTouching) playerPos = endX;
        }
        return true;
    }

    public void update() {

        if (lives <= 0) {
            Log.d("Update", "lives <= 0");

            Intent i = new Intent(context, GameOverActivity.class);
            context.startActivity(i);
            gameLoop.stopLoop();
        }

        map.update();
        //Log.d("update", "cur:" + map.getCurrent());
    }


    public void draw(Canvas c) {
        //Log.d("GameSurfaceView.java", "draw()");
        super.draw(c);



        //background
        c.drawBitmap(background, 0, 0, null);

        //Guide lines
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(6);

        c.drawLine(getLeft(), (float) linePos, getRight(), (float) linePos, red);
        c.drawLine(getWidth()/2, getBottom(), getWidth()/2, getTop(), red);

        //map
        map.draw(c);

        //rocket

        //choose image
        /* Bitmap rocket = null;
        switch (lives) {
            case 1:
                rocket = rocket1;
                break;
            case 2:
                rocket = rocket2;
                break;
            case 3:
                rocket = rocket3;
                break;
            default:
                rocket = rocket1;
                break;
        } */

        c.drawBitmap(rocket3, (float) playerPos - rocket3.getWidth()/8, (float) linePos-135, null);

        //text
        Paint text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(80);
        c.drawText("Lives: " + lives, 30, 100, text);

        c.drawRect(20, 120, 120, 220, red);

    }

    public void drawNote(Canvas c) {


    }

    public void drawGameOver(Canvas c) {
        Paint text = new Paint();
        text.setColor(Color.WHITE);
        text.setTextSize(80);
        c.drawText("GAME OVER", 30, 100, text);

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
}
