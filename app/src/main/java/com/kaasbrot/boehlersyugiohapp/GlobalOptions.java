package com.kaasbrot.boehlersyugiohapp;

import android.content.SharedPreferences;

import java.util.Random;

public class GlobalOptions {

    public enum Views {
        FIRST_VIEW(R.layout.activity_main),
        SECOND_VIEW(R.layout.activity_points);

        final int layout;
        Views(int layout) {
            this.layout = layout;
        }

        public static Views from(int layout) {
            if(layout == R.layout.activity_main) return FIRST_VIEW;
            if(layout == R.layout.activity_points) return SECOND_VIEW;
            throw new RuntimeException("Unknown layout id " + layout + ", expected " + FIRST_VIEW.layout + " or " + SECOND_VIEW.layout);
        }
    }

    private static final Random random = new Random();

    public static final String STARTING_LIFE_POINTS = "startinglifepoints";
    public static final String KEEP_SCREEN_ON = "keepscreenon";
    public static final String DELETE_AFTER_4 = "deleteafter4";
    public static final String REMEMBER_VIEW = "rememberview";
    public static final String SHEEP_COUNT = "sheepcount";
    public static final String HISTORY = "history";
    public static final String TIMER_IS_RUNNING = "timer_running";
    public static final String TIMER_PAUSE_TIME = "timer_paused";
    public static final String TIMER_START_TIME = "timer_paused";

    public static int settingstextsize = 20;

    private static SharedPreferences prefs = null;
    private static int startingLifePoints = 8000;
    private static boolean keepScreenOn = false;
    private static boolean deleteAfter4 = false;

    private static boolean timerRunning = false;
    private static long timerStartTime = 0;
    private static long timerPauseTime = 0;

    private static Views currentView = Views.FIRST_VIEW;

    // when sheepCounter hits 0 only then is there a possibility that the sheep are shown
    private static int sheepCounter = 2;
    // probability that sheep is shown, i.e., there is a 1 in 20 chance
    private static int sheepDiceSize = 20;

    public static void setPrefs(SharedPreferences prefs) {
        GlobalOptions.prefs = prefs;
        try {
            startingLifePoints = prefs.getInt(STARTING_LIFE_POINTS, 8000);
        } catch(Exception e) {
            setStartingLifePoints(8000);
        }

        try {
            keepScreenOn = prefs.getBoolean(KEEP_SCREEN_ON, false);
        } catch(Exception e) {
            setScreenAlwaysOn(false);
        }

        try {
            deleteAfter4 = prefs.getBoolean(DELETE_AFTER_4, false);
        } catch(Exception e) {
            setDeleteAfter4(false);
        }

        try {
            sheepCounter = prefs.getInt(SHEEP_COUNT, 2);
        } catch(Exception e) {
            setSheepCount(2);
        }

        try {
            int i = prefs.getInt(REMEMBER_VIEW, Views.FIRST_VIEW.layout);
            currentView = Views.from(i);
        } catch(Exception e) {
            setCurrentView(Views.FIRST_VIEW);
        }

        // TIMER STUFF
        try {
            timerRunning = prefs.getBoolean(TIMER_IS_RUNNING, false);
        } catch(Exception e) {
            setTimerRunning(false);
        }

        try {
            timerStartTime = prefs.getLong(TIMER_START_TIME, 0);
        } catch(Exception e) {
            setTimerStartTime(0);
        }

        try {
            timerPauseTime = prefs.getLong(TIMER_PAUSE_TIME, 0);
        } catch(Exception e) {
            setTimerPauseTime(0);
        }
    }

    public static void reset() {
        prefs.edit().clear().commit();
        setPrefs(prefs);
    }

    /*
     * STARTING LIFE POINTS
     */
    public static int getStartingLifePoints() {
        return startingLifePoints;
    }

    public static void setStartingLifePoints(int newLifePoints) {
        startingLifePoints = newLifePoints;
        prefs.edit().putInt(STARTING_LIFE_POINTS, newLifePoints).apply();
    }

    /*
     * SCREEN ALWAYS ON
     */
    public static boolean isScreenAlwaysOn() {
        return keepScreenOn;
    }

    public static void setScreenAlwaysOn(boolean alwaysOn) {
        keepScreenOn = alwaysOn;
        prefs.edit().putBoolean(KEEP_SCREEN_ON, alwaysOn).apply();
    }

    /*
     * DELETE AFTER 4
     */
    public static boolean isDeleteAfter4() {
        return deleteAfter4;
    }

    public static void setDeleteAfter4(boolean delete) {
        deleteAfter4 = delete;
        prefs.edit().putBoolean(KEEP_SCREEN_ON, delete).apply();
    }

    /*
     * TIMER STUFF
     */
    public static void setTimerRunning(boolean isRunning) {
        timerRunning = isRunning;
        prefs.edit().putBoolean(TIMER_IS_RUNNING, isRunning).apply();
    }

    public static void setTimerStartTime(long value) {
        timerStartTime = value;
        prefs.edit().putLong(TIMER_START_TIME, value).apply();
    }

    public static void setTimerPauseTime(long value) {
        timerPauseTime = value;
        prefs.edit().putLong(TIMER_IS_RUNNING, value).apply();
    }

    public static boolean isTimerRunning() {
        return timerRunning;
    }

    public static long getTimerStartTime() {
        return timerStartTime;
    }

    public static long getTimerPauseTime() {
        return timerPauseTime;
    }

    /*
     * LAST REMEMBERED VIEW
     */
    public static Views getCurrentView() {
        return currentView;
    }

    public static boolean isFirstView() {
        return currentView == Views.FIRST_VIEW;
    }

    public static boolean isSecondView() {
        return currentView == Views.SECOND_VIEW;
    }

    public static void setCurrentView(Views newView) {
        currentView = newView;
        prefs.edit().putInt(REMEMBER_VIEW, newView.layout).apply();
    }

    /*
     * Show sheep sheep sheep?
     */
    public static boolean showSheep() {
        if(sheepCounter == 0) {
            return random.nextInt(sheepDiceSize) == 0;
        } else {
            // don't show sheep
            sheepCounter--;
            prefs.edit().putInt(SHEEP_COUNT, sheepCounter).apply();
            return false;
        }
    }

    public static void setSheepCount(int newSheepCount) {
        sheepCounter = newSheepCount;
        prefs.edit().putInt(SHEEP_COUNT, newSheepCount).apply();
    }
}
