package com.kaasbrot.boehlersyugiohapp;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;

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

    public static final String STARTING_LIFE_POINTS = "startinglifepoints";
    public static final String KEEP_SCREEN_ON = "keepscreenon";
    public static final String DELETE_AFTER_4 = "deleteafter4";
    public static final String REMEMBER_VIEW = "rememberview";
    public static final String HISTORY = "history";

    public static int settingstextsize = 20;

    private static SharedPreferences prefs = null;
    private static int startingLifePoints = 8000;
    private static boolean keepScreenOn = false;
    private static boolean deleteAfter4 = false;
    private static Views currentView = Views.FIRST_VIEW;

    public static void setPrefs(SharedPreferences prefs) {
        GlobalOptions.prefs = prefs;
        try {
            startingLifePoints = prefs.getInt(STARTING_LIFE_POINTS, 8000);
        } catch(Exception e) {
            startingLifePoints = 8000;
            setStartingLifePoints(startingLifePoints);
        }

        try {
            keepScreenOn = prefs.getBoolean(KEEP_SCREEN_ON, false);
        } catch(Exception e) {
            keepScreenOn = false;
            setScreenAlwaysOn(keepScreenOn);
        }

        try {
            deleteAfter4 = prefs.getBoolean(DELETE_AFTER_4, false);
        } catch(Exception e) {
            deleteAfter4 = false;
            setDeleteAfter4(deleteAfter4);
        }

        try {
            int i = prefs.getInt(REMEMBER_VIEW, Views.FIRST_VIEW.layout);
            currentView = Views.from(i);
        } catch(Exception e) {
            currentView = Views.FIRST_VIEW;
            setCurrentView(currentView);
        }
    }

    /*
     * STARTING LIFE POINTS
     */
    public static int getStartingLifePoints() {
        return startingLifePoints;
    }

    public static boolean setStartingLifePoints(int newLifePoints) {
        startingLifePoints = newLifePoints;
        return prefs.edit().putInt(STARTING_LIFE_POINTS, newLifePoints).commit();
    }

    /*
     * SCREEN ALWAYS ON
     */
    public static boolean isScreenAlwaysOn() {
        return keepScreenOn;
    }

    public static boolean setScreenAlwaysOn(boolean alwaysOn) {
        keepScreenOn = alwaysOn;
        return prefs.edit().putBoolean(KEEP_SCREEN_ON, alwaysOn).commit();
    }

    /*
     * DELETE AFTER 4
     */
    public static boolean isDeleteAfter4() {
        return deleteAfter4;
    }

    public static boolean setDeleteAfter4(boolean delete) {
        deleteAfter4 = delete;
        return prefs.edit().putBoolean(KEEP_SCREEN_ON, delete).commit();
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

    public static boolean setCurrentView(Views newView) {
        currentView = newView;
        return prefs.edit().putInt(REMEMBER_VIEW, newView.layout).commit();
    }
}
