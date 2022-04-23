package com.example.multimedia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.view.MotionEventCompat;

// holds all objects of the game, implements update and render methods

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder holder;
    private GameLoop gameLoop;

    private double playerPos;
    private double linePos;

    private double charaWidth;
    private double charaHeight;

    private boolean isTouching;

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

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();

        //init
        playerPos = getWidth() / 2;
        linePos = getHeight() - 200;
        charaHeight = 100;
        charaWidth = 100;
        isTouching = false;

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
                    startY > linePos - charaHeight && startY < linePos + charaHeight) {
                //Log.d("GameSurfaceView.java", "touch x=" + startX + " y=" + startY);
                isTouching = true;
            } else { isTouching = false; }
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            endX = e.getX();

            if (isTouching) playerPos = endX;
        }
        return true;
    }

    public void update() {

    }

    private void updatePlayerPos() {

    }


    public void draw(Canvas c) {
        //Log.d("GameSurfaceView.java", "draw()");
        super.draw(c);

        // Paint erstellen:
        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(10);

        Paint blue = new Paint();
        blue.setColor(Color.BLUE);


        c.drawLine(50, (float) linePos, getWidth() - 50, (float) linePos, red);
        c.drawCircle((float) playerPos, (float) linePos, (float) charaHeight, blue);
        //c.drawRect(20, 20, 100, 100, red);
    }

    public void drawNote(Canvas c) {


    }
}
