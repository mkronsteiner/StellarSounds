package com.example.multimedia;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class LevelMap {

    GameSurfaceView view;

    int asteroidCount;
    int shieldCount;

    double leftBound;
    double rightBound;
    double bottomPos;

    double[] map;

    Bitmap asteroid, shield;

    Paint white, red, blue;

    int t;
    int bpm; //beats per minute
    double speed;

    public LevelMap(GameSurfaceView view) {
        this.view = view;
        t = 1;
        bpm = 60;
        speed = 5;


        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(30);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(30);

        blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(30);
    }

    public void initPos() {
        leftBound = view.getLeft();
        rightBound = view.getRight();
        bottomPos = view.getLinePos();
    }

    public void initBitmaps(Bitmap asteroid, Bitmap shield) {
        this.asteroid = asteroid;
        this.shield = shield;
    }

    public void loadMap() {
        // 2 = asteroid, 3 = shield
        map = new double[]{0.0, 0.0, 0.0, 0.0,
                           0.1, 0.4, 0.3, 2.5,
                           0.5, 0.6, 0.2, 3.5};
    }

    //get the value or item at the current position
    public double getCurrent() {
        double cur = t;

        if (cur >= map.length) return 0.0;

        /* double last = map[(int) Math.floor(cur)];
        double next = map[(int) Math.ceil(cur)];
        cur = interpolate(last, next, cur - Math.floor(cur));*/

        return cur;
    }

    public double interpolate(double a, double b, double pos) {
        return (b - a) * pos + a;
    }

    public void update() {
        //advance time
        t += speed;
    }

    public void draw(Canvas c) {
        int width = view.getWidth();
        float itemWidth = (float) asteroid.getWidth()/2;
        float itemHeight = (float) asteroid.getHeight()/2;

        for (int i = 0; i < map.length; i++) {
            double cur = map[i];

            //if 0 < cur < 1: draw node point
            if (cur > 0.0 && cur < 1){
                float x = (float) (leftBound + cur * width);
                float y = (float) bottomPos + t - i * 200;
                c.drawCircle(x, y, 30, white);

            //if 2 < cur < 3: draw asteroid
            } else if (cur > 2.0 && cur < 3.0) {
                float x = (float) ((leftBound + (cur-2) * width)-itemWidth);
                float y = (float) ((bottomPos + t - i * 200)-itemHeight);
                //c.drawCircle(x, y, 30, red);
                c.drawBitmap(asteroid, x, y, null);

            //if 3 < cur < 4: draw shield
            } else if (cur > 3.0 && cur < 4.0) {
                float x = (float) ((leftBound + (cur-3) * width)-itemWidth);
                float y = (float) ((bottomPos + t - i * 200)-itemHeight);
                //c.drawCircle(x, y, 30, blue);
                c.drawBitmap(shield, x, y, null);
            }

            //Log.d("LevelMap.draw", "drawing at x=" + x + " y=" + y);
        }
        //Log.d("LevelMap.draw", "done");
    }

}
