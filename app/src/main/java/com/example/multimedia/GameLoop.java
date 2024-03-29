package com.example.multimedia;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 GameLoop implements Thread methods, calls update and render methods in GameSurfaceView and calculates average FPS/UPS
 source: https://github.com/bukkalexander/AndroidStudio2DGameDevelopment
 @author Mirjam Kronsteiner
 */

public class GameLoop extends Thread {

    private boolean isRunning;
    private GameSurfaceView game;
    private SurfaceHolder surfaceHolder;

    private double fps;
    private double ups;

    public static final double MAX_UPS = 30.0;
    private static final double UPS_PERIOD = 1E+3/MAX_UPS;

    private Object o;
    private volatile boolean suspended;

    public GameLoop(GameSurfaceView game, SurfaceHolder holder) {
        this.game = game;
        surfaceHolder = holder;
        isRunning = false;

        o = new Object();
        suspended = false;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    public void pauseLoop() {
        suspended = true;
    }

    public void resumeLoop() {
        suspended = false;
        synchronized (o) {
            o.notifyAll();
        }
    }

    public void stopLoop() {
        Log.d("GameLoop", "loop stopped");
        isRunning = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        // Declare time and cycle count variables
        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        // Game loop
        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while(isRunning) {
            if (!suspended) {

                // Try to update and render game
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        game.update();
                        updateCount++;

                        game.draw(canvas);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                            frameCount++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Pause game loop to not exceed target UPS
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Skip frames to keep up with target UPS
                while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                    game.update();
                    updateCount++;
                    elapsedTime = System.currentTimeMillis() - startTime;
                    sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
                }

                // Calculate average UPS and FPS
                elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= 1000) {
                    ups = updateCount / (1E-3 * elapsedTime);
                    fps = frameCount / (1E-3 * elapsedTime);
                    updateCount = 0;
                    frameCount = 0;
                    startTime = System.currentTimeMillis();
                }
            } else {
                try {
                    while(suspended){

                        //draw pause screen
                        canvas = surfaceHolder.lockCanvas();
                        game.drawPauseScreen(canvas);
                        surfaceHolder.unlockCanvasAndPost(canvas);

                        synchronized(o){
                            o.wait();

                        }

                    }
                }
                catch (InterruptedException e) {

                }
            }
        }
    }

    public double getFps() {
        return fps;
    }


}
