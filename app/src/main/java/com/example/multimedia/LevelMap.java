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

/**
 * LevelMap holds one game level and handles drawing and interaction with game objects (items and points)
 * @author Mirjam Kronsteiner
 */

public class LevelMap {

    GameSurfaceView view;

    private double leftBound;
    private double bottomPos;
    private Bitmap player;
    private int playerX, playerY;

    private double[] map;
    private double[] vertPos;
    private int width;
    private float itemWidth, itemHeight, starWidth, starHeight;

    private Bitmap asteroid, shield, star;

    final private Paint white, red, blue, whiteLine;

    //variables for current value calculation
    private int t; //time
    final private double speed; //scrolling speed
    final private double margin; //vertical margin between elements
    private int index;
    private double temp;
    private double current;
    private boolean touching;
    private int touchingCounter;

    public LevelMap(GameSurfaceView view) {
        this.view = view;

        t = 0;
        speed = 10;
        margin = 200;
        index = 0;
        temp = 0;
        current = 0;
        touching = false;
        touchingCounter = 0; //wait a few frames at the beginning of a note

        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(10);

        whiteLine = new Paint();
        whiteLine.setColor(Color.WHITE);
        whiteLine.setStrokeWidth(10);
        whiteLine.setStyle(Paint.Style.STROKE);

        red = new Paint();
        red.setColor(Color.RED);
        red.setStrokeWidth(10);

        blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(10);

    }

    /**
     * load positions from GameSurfaceView
     */
    public void initPos() {
        leftBound = view.getLeft();
        bottomPos = view.getLinePos();
        player = view.getPlayer();
        playerX = view.getPlayerX();
        playerY = view.getPlayerY();

        width = view.getWidth();

    }

    public void initBitmaps(Bitmap asteroid, Bitmap shield, Bitmap star) {
        this.asteroid = asteroid;
        this.shield = shield;
        this.star = star;

        itemWidth = (float) asteroid.getWidth();
        itemHeight = (float) asteroid.getHeight();
        starWidth = (float) star.getWidth();
        starHeight = (float) star.getHeight();
    }

    /**
     * initialize level data
     * @param mapData array containing the level data values
     */
    public void loadMap(double[] mapData) {

        map = mapData;

        //array of the same length holding vertical positions of elements
        vertPos = new double[map.length];

        int j = 2;
        for (int i = 0; i < map.length; i++) {
            vertPos[i] = bottomPos - j * margin;
            j++;
        }
    }

    /**
     * advance the position of the elements
     * check for Game Win if the level has ended
     * calculate the current value at the position of the rocket (linePos)
     */
    public void update() {
        //advance time
        t += speed;

        //move all the elements
        for (int i = 0; i < map.length; i++) {
            vertPos[i] += speed;
        }

        //calculate the current value of the note at the line (bottom) position
        //index = index of the current element in the level map
        double indexc = (t/margin)-1;
        index = (int) Math.floor(indexc);

        //if the map has ended, go to game win state
        if (index >= map.length-1) {
            Log.d("LevelMap.update()", "map ended");
            view.gameWin();
        }

        //temp = value at the current index
        if (index >= 0 && index < map.length-1) {
            int prev = index-1;
            int next = index;
            temp = map[index];

            //if a note is currently being touched
            if (temp > 1 && temp < 2) {

                //fix for wrong calculation: wait a few frames before setting touching=true
                if (!touching && touchingCounter == 0) {
                    touchingCounter = 20;
                } else if (!touching && touchingCounter > 1) {
                    touchingCounter--;
                } else if (!touching && touchingCounter == 1){
                    touching = true;
                    touchingCounter--;
                } else {
                    touching = true;
                }

                //interpolate between previous and this node
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

    /**
     * get the element at the current position
     */
    public double getCurrent() {
        return current;
    }

    public double interpolate(double a, double b, double pos) {
        return (b - a) * pos + a;
    }

    /**
     * for points calculation
     * @return if a note is being read this frame (and not an item or empty space)
     */
    public boolean isTouching() { return touching; }

    /**
     * draw all level elements and check for collisions with items
     * @param c Canvas
     */
    public void draw(Canvas c) {

        playerX = view.getPlayerX();
        //Log.d("LevelMap.draw", "playerX= " + playerX);

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

        //draw items & note points
        double cur;
        for (int i = 0; i < map.length; i++) {
            cur = map[i];

            //only render elements in view
            if (vertPos[i] <= view.getBottom() && vertPos[i] >= view.getTop()) {

                //if 1 < cur < 2: draw node point, decimals determine x-position
                if (cur > 1 && cur < 2) {
                    float x = (float) (leftBound + (cur - 1) * width);
                    float y = (float) vertPos[i];
                    c.drawBitmap(star,x  - starHeight/2, y - starWidth/2, null);
                    //c.drawCircle(x, y, 30, y <= bottomPos ? white : red);
                    //c.drawLine(view.getLeft(), y, view.getRight(), y, red);
                    //Log.d("LevelMap.draw()", "drawing point at" + x);

                //if 2 < cur < 3: draw asteroid, decimals determine x-position
                } else if (cur > 2.0 && cur < 3.0) {
                    float x = (float) ((leftBound + (cur - 2) * width));
                    float y = (float) vertPos[i] - itemHeight;

                    if (isCollisionDetected(player, playerX, playerY, asteroid, (int) x, (int) y)) {
                        Log.d("LevelMap.draw()", "collide with asteroid");
                        view.collideWithAsteroid();
                        map[i] = 0.0;
                    }

                    c.drawBitmap(asteroid, x  - itemWidth/2, y, null);
                    //Log.d("LevelMap.draw", "drawing asteroid at x=" + x + " y=" + y);


                //if 3 < cur < 4: draw shield, decimals determine x-position
                } else if (cur > 3.0 && cur < 4.0) {
                    float x = (float) ((leftBound + (cur - 3) * width));
                    float y = (float) vertPos[i] - itemHeight;

                    if (isCollisionDetected(player, playerX, playerY, shield, (int) x, (int) y)) {
                        Log.d("LevelMap.draw()", "collide with shield");
                        view.collideWithShield();
                        map[i] = 0.0;
                    }

                    c.drawBitmap(shield, x  - itemWidth/ 2, y, null);
                    //Log.d("LevelMap.draw", "drawing shield at x=" + x + " y=" + y);
                }
            }

        }


        //Log.d("LevelMap.draw", "done");
    }


    /**
     * Check for collision of two Bitmaps
     * source: https://stackoverflow.com/a/7430359
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
