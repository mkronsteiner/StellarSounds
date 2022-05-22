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

    double leftBound;
    double rightBound;
    double bottomPos;

    double[] map;
    double[] vertPos;

    Bitmap asteroid, shield;

    Paint white, red, blue;

    int t;
    int bpm; //beats per minute
    double speed;
    double margin;

    //variables for current value calculation
    int index;
    double temp;
    double current;

    public LevelMap(GameSurfaceView view) {
        this.view = view;
        t = 0;
        bpm = 60;
        speed = 5;
        margin = 200;

        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(10);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(10);

        blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(10);

        index = 0;
        temp = 0;
        current = 0;
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
        //horizontal positions of map elements are assigned in the map array, vertical positions are stored in vertPos(and changed per frame)
        //0 = empty, 1 = point, 2 = asteroid, 3 = shield

        //positions on x axis
        map = new double[] {0.0, 0.0, 0.0, 0.0,
                            1.1, 1.4, 1.3, 2.5,
                            0.0, 0.0, 0.0, 0.0,
                            1.5, 1.6, 1.2, 3.5};

        //positions y axis
        vertPos = new double[map.length];

        int j = 2;
        for (int i = 0; i < map.length; i++) {
            vertPos[i] = bottomPos - j * margin;
            j++;
        }
    }

    //get the value or item at the current position
    public double getCurrent() {
        return current;
    }

    public double interpolate(double a, double b, double pos) {
        return (b - a) * pos + a;
    }

    public void update() {
        //advance time
        t += speed;

        for (int i = 0; i < map.length; i++) {
            vertPos[i] += speed;
        }

        //calculate the current value of the note at the line (bottom) position
        double indexc = (t-bottomPos)/margin;
        index = (int) Math.floor(indexc);

        if (index >= 0 && index < map.length) {
            int prev = index + 4;
            int next = index + 5;
            temp = map[index+5];
            if (temp > 1 && temp < 2) {
                double pos = indexc - Math.floor(indexc);
                current = interpolate(map[prev], map[next], pos);
                current = (Math.round((current - Math.floor(current))*100))/100.0;
            } else { current = 0.0; }
        } else if (index > map.length) {
            current = 0.0;
        }



        Log.d("LevelMap.update()", "Current: " + current);
    }

    public void draw(Canvas c) {
        int width = view.getWidth();
        float itemWidth = (float) asteroid.getWidth()/2;
        float itemHeight = (float) asteroid.getHeight()/2;

        //draw items & note points
        double cur;
        for (int i = 0; i < map.length; i++) {
            cur = map[i];

            //only render elements in view
            if (vertPos[i] <= view.getBottom() && vertPos[i] >= view.getTop()) {

                //if 0 < cur < 1: draw node point, decimals determine x-position
                if (cur > 1 && cur < 2) {
                    float x = (float) (leftBound + (cur - 1) * width);
                    //float y = (float) bottomPos + t - i * 200;
                    float y = (float) vertPos[i];
                    c.drawCircle(x, y, 30, y <= bottomPos ? white : red);

                //if 2 < cur < 3: draw asteroid, decimals determine x-position
                } else if (cur > 2.0 && cur < 3.0) {
                    float x = (float) ((leftBound + (cur - 2) * width) - itemWidth);
                    //float y = (float) ((bottomPos + t - i * 200)-itemHeight);
                    float y = (float) vertPos[i] - itemHeight;
                    //c.drawCircle(x, y, 30, red);
                    c.drawBitmap(asteroid, x, y, null);

                //if 3 < cur < 4: draw shield, decimals determine x-position
                } else if (cur > 3.0 && cur < 4.0) {
                    float x = (float) ((leftBound + (cur - 3) * width) - itemWidth);
                    //float y = (float) ((bottomPos + t - i * 200)-itemHeight);
                    float y = (float) vertPos[i] - itemHeight;
                    //c.drawCircle(x, y, 30, blue);
                    c.drawBitmap(shield, x, y, null);
                }
            }
            //Log.d("LevelMap.draw", "drawing at x=" + x + " y=" + y);
        }

        //draw note edges
        float lastX, lastY, nextX, nextY;
        for (int i = 1; i < map.length; i++) {
            //check if this and the previous node are note points and not items/empty
            if (map[i-1] > 1 && map[i-1] < 2 && map[i] > 1 && map[i] < 2) {
                lastX = (float) (leftBound + (map[i-1] -1) * width);
                lastY = (float) vertPos[i-1];
                nextX = (float) (leftBound + (map[i] - 1) * width);
                nextY = (float) vertPos[i];
                c.drawLine(lastX, lastY, nextX, nextY, nextY <= bottomPos ? white : red);
            }
        }

        //Log.d("LevelMap.draw", "done");
    }

}
