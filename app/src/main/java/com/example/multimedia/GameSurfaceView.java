package com.example.multimedia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// holds all objects of the game, implements update and render methods

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    private SurfaceHolder holder;
    private GameLoop gameLoop;

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

        //init game objects
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();

    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
    // Ein Touch-Event wurde ausgelöst
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
        // Was soll bei einer Berührung passieren?
        }
        return true;
    }

    public void update() {

    }


    public void draw(Canvas c) {
        Log.d("GameSurfaceView.java", "draw()");
        super.draw(c);

        // Paint erstellen:
        Paint p = new Paint();
        p.setColor(Color.RED);
        p.setTextSize(20);

        c.drawRect(20, 20, 100, 100, p);
        //c.drawText("Test", 150, 40, p);
    }
}
