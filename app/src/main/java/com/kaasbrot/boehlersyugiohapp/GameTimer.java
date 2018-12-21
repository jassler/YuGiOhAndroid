package com.kaasbrot.boehlersyugiohapp;

import android.os.Handler;

public class GameTimer {

    private Handler handler;
    private long started;
    private GameTimerUpdateListener updateListener;

    private boolean running;

    private Runnable repeatingCall = new Runnable() {
        @Override
        public void run() {
            long secondsPassed = getSecondsPassed();
            updateListener.timerUpdate(formatSeconds(secondsPassed));

            // call function again in one second
            handler.postDelayed(repeatingCall, 250);
        }
    };

    public GameTimer() {
        handler = new Handler();
        running = false;
    }

    public void startTimer(GameTimerUpdateListener updateListener) {
        started = System.currentTimeMillis();

        this.updateListener = updateListener;
        if(running)
            handler.removeCallbacks(repeatingCall);

        running = true;
        repeatingCall.run();
    }

    public void stopTimer() {
        if(running) {
            handler.removeCallbacks(repeatingCall);
            running = false;
        }
    }

    public long getSecondsPassed() {
        return (System.currentTimeMillis() - started) / 1000;
    }

    public String getSecondsPassedString() {
        return formatSeconds(getSecondsPassed());
    }

    public boolean isRunning() {
        return running;
    }

    public static String formatSeconds(long seconds) {
        if(seconds < 60*60) {
            return String.format("%02d:%02d", seconds / 60, seconds % 60);
        }
        return String.format("%d:%02d:%02d", seconds / (60*60), (seconds / 60) % 60, seconds % 60);
    }

    public interface GameTimerUpdateListener {
        void timerUpdate(String newValue);
    }

}
