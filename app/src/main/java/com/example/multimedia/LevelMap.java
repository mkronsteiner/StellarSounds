package com.example.multimedia;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class LevelMap {

    GameSurfaceView view;

    private double leftBound;
    private double bottomPos;
    private Bitmap player;
    private int playerX, playerY;

    private double[] map;
    private double[] vertPos;

    private Bitmap asteroid, shield;

    final private Paint white, red, blue;

    //variables for current value calculation
    private int t; //time
    final private double speed; //scrolling speed
    final private double margin; //vertical margin between elements
    private int index;
    private double temp;
    private double current;
    private boolean touching;

    public LevelMap(GameSurfaceView view) {
        this.view = view;

        t = 0;
        speed = 10;
        margin = 200;
        index = 0;
        temp = 0;
        current = 0;
        touching = false;

        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(10);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(10);

        blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(10);
    }

    public void initPos() {
        leftBound = view.getLeft();
        bottomPos = view.getLinePos();
        player = view.getPlayer();
        playerX = view.getPlayerX();
        playerY = view.getPlayerY();
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
                            1.1, 1.4, 1.3, 3.5,
                            0.0, 0.0, 0.0, 0.0,
                            1.5, 1.6, 1.2, 2.5,
                            0.0, 0.0, 0.0, 0.0,
                            0.0, 0.0, 0.0, 0.0};

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
                touching = true;
                double pos = indexc - Math.floor(indexc);
                current = interpolate(map[prev], map[next], pos);
                current = (Math.round((current - Math.floor(current))*100))/100.0;
            } else {
                current = 0.0;
                touching = false;
            }
        } else if (index > map.length) {
            current = 0.0;
            touching = false;
        }



        //Log.d("LevelMap.update()", "Current: " + current);
    }

    public boolean isTouching() { return touching; }

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
                    float y = (float) vertPos[i];
                    c.drawCircle(x, y, 30, y <= bottomPos ? white : red);

                //if 2 < cur < 3: draw asteroid, decimals determine x-position
                } else if (cur > 2.0 && cur < 3.0) {
                    float x = (float) ((leftBound + (cur - 2) * width) - itemWidth);
                    float y = (float) vertPos[i] - itemHeight;

                    if (isCollisionDetected(player, 260, playerY, asteroid, (int) x, (int) y)) {
                        //Log.d("LevelMap.draw()", "collide with asteroid");
                        view.collideWithAsteroid();
                        map[i] = 0.0;
                    }

                    c.drawBitmap(asteroid, x, y, null);

                //if 3 < cur < 4: draw shield, decimals determine x-position
                } else if (cur > 3.0 && cur < 4.0) {
                    float x = (float) ((leftBound + (cur - 3) * width) - itemWidth);
                    float y = (float) vertPos[i] - itemHeight;

                    if (isCollisionDetected(player, 260, playerY, shield, (int) x, (int) y)) {
                        //Log.d("LevelMap.draw()", "collide with shield");
                        view.collideWithShield();
                        map[i] = 0.0;
                    }

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


    //COLLISION DETECTION
    /**
     * @param bitmap1 First bitmap
     * @param x1 x-position of bitmap1 on screen.
     * @param y1 y-position of bitmap1 on screen.
     * @param bitmap2 Second bitmap.
     * @param x2 x-position of bitmap2 on screen.
     * @param y2 y-position of bitmap2 on screen.
     */
    public boolean isCollisionDetected(Bitmap bitmap1, int x1, int y1,
                                       Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect(x1, y1, x1+bitmap1.getWidth(), y1+bitmap1.getHeight());
        Rect bounds2 = new Rect(x2, y2, x2+bitmap2.getWidth(), y2+bitmap2.getHeight());

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }
}
