package com.kaasbrot.boehlersyugiohapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.Key;
import java.util.Calendar;
import java.util.Random;

import static com.kaasbrot.boehlersyugiohapp.GameInformation.history;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p1;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p2;

public class MainActivity extends AppCompatActivity implements ButtonDeterminer {

    MenuItem redoButton;
    MenuItem undoButton;
    MenuItem timerShowButton;

    int currentContentView;
    int currentMenu;

    Toolbar toolbar;

    EditText timerText;

    // counts seconds passed since game started
    GameTimer gameTimer;
    Random rand;

    ActionHistoryDialog historyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentContentView = R.layout.activity_main;
        setContentView(currentContentView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        currentMenu = R.menu.menu_main;

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        gameTimer = new GameTimer();
        rand = new Random();
        updateComponentActivities();

        historyDialog = new ActionHistoryDialog();
        historyDialog.setHistory(history);
    }

    /**
     * When loading view for the first time or switching views, make sure
     * all components points to the correct view elements on screen.
     */
    private void updateComponentActivities() {
        p1.updateActivity(
                findViewById(R.id.pointsPlayer1),
                findViewById(R.id.tmpText1),
                this
        );
        p2.updateActivity(
                findViewById(R.id.pointsPlayer2),
                findViewById(R.id.tmpText2),
                this
        );
        gameTimer.updateActivity(
                findViewById(R.id.viewTimer),
                findViewById(R.id.timerText),
                findViewById(R.id.timerPlayPauseButton)
        );


        // don't show title "YuGiCalc" in the title bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // when editing time counter, make sure it behaves correctly
        // - no invalid input
        timerText = findViewById(R.id.timerText);
        timerText.setSelectAllOnFocus(true);
        timerText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                if (gameTimer.isRunning()) {
                    gameTimer.toggleTimer();
                }
            }
        });

        timerText.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                clearTimerTextFocus();
            }

            return true;
        });

    }

    /**
     * Clear focus from timer text, hide keyboard.
     * Update time to the one the user has typed in.
     */
    private void clearTimerTextFocus() {
        timerText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(timerText.getApplicationWindowToken(), 0);

        // hours:minutes:seconds
        String[] times = timerText.getText().toString().split(":");

        // for error messages
        Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 10);

        if (times.length > 3) {
            toast.setText(R.string.timer_err_too_many_columns);
            toast.show();
            return;
        }

        int seconds = 0;
        for (String time : times) {
            try {
                seconds *= 60;
                seconds += Integer.parseInt(time);
            } catch (NumberFormatException e) {
                toast.setText(R.string.timer_err_invalid_symbol);
                toast.show();
                return;
            }
        }

        // no more than 5 hours!
        if (seconds > 5 * 60 * 60) {
            toast.setText(R.string.timer_err_max_exceeded);
            toast.show();
            return;
        }
        gameTimer.setSecondsPassed(seconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(currentMenu, menu);
        redoButton = menu.findItem(R.id.action_redo);
        undoButton = menu.findItem(R.id.action_undo);
        timerShowButton = menu.findItem(R.id.action_start_timer);

        MenuItem timerButton = menu.findItem(R.id.action_start_timer);
        if(gameTimer.isTimerVisible()) {
            timerButton.setTitle(R.string.hide_timer);
        } else {
            timerButton.setTitle(R.string.show_timer);
        }

        // clock from here: https://stackoverflow.com/questions/5437674/what-unicode-characters-represent-time
        // setTitle("⏰");

        // check if undo/redo Buttons should be enabled
        determineButtonEnable();

        return true;
    }

    /**
     * Since Toolbar buttons directly call methods, this can be ignored.
     * I just left it here IN CASE! (you never know)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if undo / redo buttons should be enabled.
     * @param menu
     * @return super
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem undoButton = menu.findItem(R.id.action_undo);
        MenuItem redoButton = menu.findItem(R.id.action_redo);

        if(history.canUndo()) {
            undoButton.setEnabled(true);
            undoButton.getIcon().setAlpha(255);
        } else {
            undoButton.setEnabled(false);
            undoButton.getIcon().setAlpha(50);
        }

        if(history.canRedo()) {
            redoButton.setEnabled(true);
            redoButton.getIcon().setAlpha(255);
        } else {
            redoButton.setEnabled(false);
            redoButton.getIcon().setAlpha(50);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * From button press, determine in which player-constraint-layout this button resides.
     * If button is inside viewPlayer1, return p1 object, else p2.
     *
     * @param v Button or view inside constraint layout
     * @return p1 or p2 object
     */
    private Player playerFromView(View v) {
        if(((ConstraintLayout) v.getParent()).getId() == R.id.viewPlayer1) {
            return p1;
        } else {
            return p2;
        }
    }


    /**
     * Called from View 1
     * If eg. +500 is tapped, add 500 points to corresponding player.
     * Corresponding player is determined by the layout container where the button is placed.
     * @param v instanceof Button
     */
    public void calculate(View v) {
        String content = ((Button) v).getText().toString().replace(" ", "");
        int number = Integer.parseInt(content);
        playerFromView(v).calculate(number, true);
    }

    /**
     * Subtract half the points from corresponding player.
     * @param v
     */
    public void div(View v) {
        playerFromView(v).divide();
    }

    /**
     * Called from View 2
     * Parse Number from text field.
     * Add or subtract number from player depending on factor (either 1 or -1).
     * Reset text field.
     * @param p Affected player
     * @param factor Either 1 do add points or -1 do subtract points
     */
    private void customInput(Player p, int factor) {
        TextView inputField = findViewById(R.id.customInput);
        if(inputField == null)
            return;

        int amount = Integer.parseInt(inputField.getText().toString());
        p.calculate(factor * amount, false);
        inputField.setText("0");
    }

    /**
     * Called from View 2
     * Add Number from text field to corresponding player.
     * @param v Plus-button inside player-constraint-layout
     */
    public void addPoint(View v) {
        customInput(playerFromView(v), 1);
    }

    /**
     * Called from View 2
     * Subtract Number from text field from corresponding player.
     * @param v Minus-button inside player-constraint-layout
     */
    public void subPoint(View v) {
        customInput(playerFromView(v), -1);
    }

    /**
     * Called from View 2
     * A button is pressed from the viewDialpad (except for the delete button)
     * Append number to text field. Amount cannot exceed certain limit (which is 100_000 right now).
     * @param v instanceof Button
     */
    public void customInputButton(View v) {
        TextView inputField = findViewById(R.id.customInput);
        if(inputField == null)
            return;

        try {
            String current = inputField.getText().toString();
            String buttonText = ((Button) v).getText().toString();
            int newAmount = Integer.parseInt(current + buttonText);

            // only set number if it doesn't exceed limit
            if(newAmount < 100000)
                inputField.setText(Integer.toString(newAmount));

        } catch(NumberFormatException e) {
            /* Probably a button is pressed that doesn't contain a number */
        }
    }

    /**
     * Called from View 2
     * Remove right-most character from text field.
     * If text field is only one character long, set it to 0.
     * @param v Unimportant
     */
    public void customInputDelete(View v) {
        TextView inputField = findViewById(R.id.customInput);
        if(inputField == null)
            return;

        if(inputField.getText().length() <= 1) {
            inputField.setText("0");
        } else {
            inputField.setText(inputField.getText().subSequence(0, inputField.getText().length() - 1));
        }
    }

    /**
     * Called from View 2
     * Set points of players to points currently in text field.
     *
     * @param v Set points for player in view v
     */
    public void customInputSet(View v) {
        TextView inputField = findViewById(R.id.customInput);
        if(inputField == null)
            return;

        try {
            int newAmount = Integer.parseInt(inputField.getText().toString());
            Player p = playerFromView(v);
            int diff = newAmount - p.points;
            p.calculate(diff, false);
            inputField.setText("0");

        } catch(NumberFormatException e) {
            /* Probably a button is pressed that doesn't contain a number */
        }
    }

    /**
     * Cancel animation and waiting timers from players.
     * If animation is running, it pretends that the animation was already finished.
     * If wait timer is on, it is canceled and ignores previous input.
     */
    private void cancelTimers() {
        p1.cancelTimer();
        p2.cancelTimer();
    }

    /**
     * Called from Toolbar
     * Cancel timers, animations. Reset points.
     * @param item
     */
    public void reset(MenuItem item) {
        p1.reset(8000);
        p2.reset(8000);

        history.add(p1.points, p2.points);
    }

    /**
     * Undo or Redo button pressed -> update player's points text field
     * @param newPoints
     */
    private void historyAction(ActionHistory.Points newPoints) {
        p1.points = newPoints.p1;
        p2.points = newPoints.p2;

        p1.updatePointsText();
        p2.updatePointsText();

        determineButtonEnable();
    }

    /**
     * Called from Toolbar
     * Undo last action. If undo's not possible (eg. at the beginning), nothing happens.
     * @param item
     */
    public void undo(MenuItem item) {
        cancelTimers();
        historyAction(history.undo());
    }

    /**
     * Called from Toolbar
     * Redo last action (after undo). If redo's not possible, nothing happens.
     * @param item
     */
    public void redo(MenuItem item) {
        cancelTimers();
        historyAction(history.redo());
    }

    /**
     * Called from Toolbar
     * If timer is running, stop and hide timer.
     * If timer is not running, start and update timer display every second.
     * @param item Timer menu item. Text is updated whether timer is shown or not.
     */
    public void toggleTimerVisibility(MenuItem item) {
        if (gameTimer.isTimerVisible()) {
            clearTimerTextFocus();
        }
        gameTimer.toggleTimerVisibility(item);
    }

    public void toggleTimer(View v) {
        // hide keyboard when user is editing time
        if (timerText.isFocused()) {
            clearTimerTextFocus();
        }
        gameTimer.toggleTimer();
    }

    public void resetTimer(View v) {
        gameTimer.resetTimer();
    }

    /**
     * TODO: Settings dialog. Put it in an extra activity or in a dialog?
     * @param item Clicked menu item (unimportant)
     */
    public void showSettings(MenuItem item) {
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        int zahl = rand.nextInt(6) + 1;
        String kopf = (rand.nextInt(2) == 0 ? "Kopf" : "Zahl");
        builder.setMessage("Die gewürfelte Zahl ist " + zahl + "\nKopf oder Zahl? -> " + kopf)
                .setTitle("Krass richtige Einstellungen!");

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int y1, y2;
    static final int MIN_DISTANCE = 50;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int deltaY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                y2 = (int) event.getY();
                deltaY = y2 - y1;
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    if (gameTimer.isTimerVisible()) {
                        clearTimerTextFocus();
                    }
                    gameTimer.toggleTimerVisibility(timerShowButton);
                } else {
                    View v = findViewById(R.id.viewTimer);
                    ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                    gameTimer.animateTimerMovement(layout.topMargin, gameTimer.getCurrentMarginTop(), layout);
                    // consider as something else - a screen tap for example
                }
                break;
            case MotionEvent.ACTION_MOVE:
                y2 = (int) event.getY();
                deltaY = y2 - y1;
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    View v = findViewById(R.id.viewTimer);
                    ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                    int left = layout.leftMargin, bottom = layout.bottomMargin, right = layout.rightMargin;

                    if (deltaY > 0 && !gameTimer.isTimerVisible()) {
                        // down gesture, show timer
                        layout.setMargins(left, deltaY - MIN_DISTANCE + gameTimer.getCurrentMarginTop(), right, bottom);
                    } else if (deltaY < 0 && gameTimer.isTimerVisible()) {
                        // up gesture, hide timer
                        layout.setMargins(left, deltaY + MIN_DISTANCE + gameTimer.getCurrentMarginTop(), right, bottom);
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Check if undo, redo buttons have to be enabled or not.
     * By invalidating the options menu, {@link #onPrepareOptionsMenu(Menu)} is called.
     */
    public void determineButtonEnable() {
        runOnUiThread(() -> invalidateOptionsMenu());
    }

    /**
     * Called from Toolbar
     * Show game history in dialog
     *
     * @param item Menu Item
     */
    public void showHistory(MenuItem item) {
        historyDialog.show(getSupportFragmentManager(), "Hisory Dialog");
    }

    /**
     * Called from Toolbar
     * If view 1 is displayed, change to view 2 and vice versa
     * Almost all the same stuff as in {@link #onCreate(Bundle)} has to be done here
     * @param item Clicked menu item (unimportant)
     */
    public void changeView(MenuItem item) {
        // use Intent if we come to a completely different activity
        // since activity stays the same (only view changes), I leave this commented
        // Intent ganesh = new Intent(this, PointsActivity.class);
        // startActivity(ganesh);

        // new toolbar id
        int toolbar_id;

        // find values
        if(currentContentView == R.layout.activity_main) {
            currentContentView = R.layout.activity_points;
            toolbar_id = R.id.toolbar_points;
            currentMenu = R.menu.menu_points;
        } else {
            currentContentView = R.layout.activity_main;
            toolbar_id = R.id.toolbar_main;
            currentMenu = R.menu.menu_main;
        }
        setContentView(currentContentView);

        toolbar = findViewById(toolbar_id);
        setSupportActionBar(toolbar);

        updateComponentActivities();
    }
}
