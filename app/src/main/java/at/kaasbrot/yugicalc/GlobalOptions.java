package at.kaasbrot.yugicalc;

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
    public static final String PLAYER_NAME_1 = "playername1";
    public static final String PLAYER_NAME_2 = "playername2";
    public static final String KEEP_SCREEN_ON = "keepscreenon";
    public static final String DELETE_AFTER_4 = "deleteafter4";
    public static final String SHOW_NAMES = "shownames";
    public static final String REMEMBER_VIEW = "rememberview";
    public static final String SHEEP_COUNT = "sheepcount";
    public static final String HISTORY = "history";
    public static final String SHOW_ACTIONS = "show_actions";
    public static final String TIMER_IS_RUNNING = "timer_running";
    public static final String TIMER_IS_VISIBLE = "timer_visible";
    public static final String TIMER_PAUSE_TIME = "timer_paused";
    public static final String TIMER_START_TIME = "timer_started";
    public static String LANGUAGE = "language";

    public static int settingstextsize;
    public static int fakeswitchimagesize;

    private static SharedPreferences prefs = null;
    private static SharedPreferences.Editor editor = null;
    private static int startingLifePoints = 8000;
    private static String playerName1 = "Player 1";
    private static String playerName2 = "Player 2";
    private static String language = "none";
    private static boolean keepScreenOn = false;
    private static boolean deleteAfter4 = false;
    private static boolean showNames = false;
    private static boolean showActions = true;

    private static boolean timerRunning = false;
    private static boolean timerVisible = false;
    private static long timerStartTime = 0;
    private static long timerPauseTime = 0;

    private static Views currentView = Views.FIRST_VIEW;

    // when sheepCounter hits 0 only then is there a possibility that the sheep are shown
    private static int sheepCounter = 2;
    // probability that sheep is shown, i.e., there is a 1 in 20 chance
    private static int sheepDiceSize = 20;

    public static void setPrefs(SharedPreferences prefs) {
        GlobalOptions.prefs = prefs;
        GlobalOptions.editor = prefs.edit();
        loadData();
    }

    private static void loadData() {
        try {
            startingLifePoints = prefs.getInt(STARTING_LIFE_POINTS, 8000);
        } catch(Exception e) {
            setStartingLifePoints(8000);
        }

//        try {
//            playerName1 = prefs.getString(PLAYER_NAME_1, "");
//        } catch(Exception e) {
//            setPlayerName1("");
//        }
//
//        try {
//            playerName2 = prefs.getString(PLAYER_NAME_2, "");
//        } catch(Exception e) {
//            setPlayerName2("");
//        }

        try {
            showActions = prefs.getBoolean(SHOW_ACTIONS, true);
        } catch(Exception e) {
            setActionsShown(true);
        }

        try {
            keepScreenOn = prefs.getBoolean(KEEP_SCREEN_ON, true);
        } catch(Exception e) {
            setScreenAlwaysOn(true);
        }

        try {
            deleteAfter4 = prefs.getBoolean(DELETE_AFTER_4, true);
        } catch(Exception e) {
            setDeleteAfter4(true);
        }

        try {
            showNames = prefs.getBoolean(SHOW_NAMES, false);
        } catch(Exception e) {
            setShowNames(false);
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
            timerStartTime = prefs.getLong(TIMER_START_TIME, 0);
            timerPauseTime = prefs.getLong(TIMER_PAUSE_TIME, 0);
        } catch(Exception e) {
            setTimerValues(false, 0, 0);
        }

        try {
            timerVisible = prefs.getBoolean(TIMER_IS_VISIBLE, false);
        } catch(Exception e) {
            setTimerVisible(false);
        }

        //LANGUAGE
        try {
            language = prefs.getString(LANGUAGE, "none");
        } catch(Exception e) {
            setLanguage("none");
        }
    }

    public static void reset() {
        editor.clear().commit();
        loadData();
    }

    /*
     * STARTING LIFE POINTS
     */
    public static int getStartingLifePoints() {
        return startingLifePoints;
    }

    public static void setStartingLifePoints(int newLifePoints) {
        startingLifePoints = newLifePoints;
        editor.putInt(STARTING_LIFE_POINTS, newLifePoints).apply();
    }

    /*
     * PLAYER NAME 1
     */
    public static String getPlayerName1() {
        return playerName1;
    }

    public static void setPlayerName1(String newPlayerName1) {
        if(playerName1.equals(newPlayerName1)) return;
        playerName1 = newPlayerName1;
        editor.putString(PLAYER_NAME_1, newPlayerName1).apply();
    }

    /*
     * PLAYER NAME 2
     */
    public static String getPlayerName2() {
        return playerName2;
    }

    public static void setPlayerName2(String newPlayerName2) {
        if(playerName2.equals(newPlayerName2)) return;
        playerName2 = newPlayerName2;
        editor.putString(PLAYER_NAME_2, newPlayerName2).apply();
    }

    /*
     * LANGUAGE
     */
    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String newLanguage) {
        if(language.equals(newLanguage)) return;
        language = newLanguage;
        editor.putString(LANGUAGE, newLanguage).apply();
    }

    /*
     * SHOW ACTIONS IN HISTORY DIALOG
     */
    public static boolean isActionsShown() {
        return showActions;
    }

    public static void setActionsShown(boolean isShown) {
        showActions = isShown;
        editor.putBoolean(SHOW_ACTIONS, isShown).apply();
    }

    /*
     * SCREEN ALWAYS ON
     */
    public static boolean isScreenAlwaysOn() {
        return keepScreenOn;
    }

    public static void setScreenAlwaysOn(boolean alwaysOn) {
        keepScreenOn = alwaysOn;
        editor.putBoolean(KEEP_SCREEN_ON, alwaysOn).apply();
    }

    /*
     * DELETE AFTER 4
     */
    public static boolean isDeleteAfter4() {
        return deleteAfter4;
    }

    public static void setDeleteAfter4(boolean delete) {
        deleteAfter4 = delete;
        editor.putBoolean(DELETE_AFTER_4, delete).apply();
    }

    /*
     * SHOW NAMES
     */
    public static boolean isShowNames() {
        return showNames;
    }

    public static void setShowNames(boolean showname) {
        showNames = showname;
        editor.putBoolean(SHOW_NAMES, showname).apply();
    }

    /*
     * TIMER STUFF
     */
    public static void setTimerValues(boolean isRunning, long startTime, long pauseTime) {
        // these are often set in conjunction, hence why they are put together into one function here
        timerRunning = isRunning;
        timerStartTime = startTime;
        timerPauseTime = pauseTime;
        editor
                .putBoolean(TIMER_IS_RUNNING, isRunning)
                .putLong(TIMER_START_TIME, startTime)
                .putLong(TIMER_PAUSE_TIME, pauseTime)
                .apply();
    }

    public static void setTimerVisible(boolean isVisible) {
        timerVisible = isVisible;
        editor.putBoolean(TIMER_IS_VISIBLE, isVisible).apply();
    }

    public static boolean isTimerRunning() {
        return timerRunning;
    }

    public static boolean isTimerVisible() {
        return timerVisible;
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
        editor.putInt(REMEMBER_VIEW, newView.layout).apply();
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
            editor.putInt(SHEEP_COUNT, sheepCounter).apply();
            return false;
        }
    }

    public static void setSheepCount(int newSheepCount) {
        sheepCounter = newSheepCount;
        editor.putInt(SHEEP_COUNT, newSheepCount).apply();
    }
}
