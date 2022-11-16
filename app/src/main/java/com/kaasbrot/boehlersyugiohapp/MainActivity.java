package com.kaasbrot.boehlersyugiohapp;

import static com.kaasbrot.boehlersyugiohapp.GameInformation.history;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p1;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p2;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaasbrot.boehlersyugiohapp.dialog.CasinoDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.CoinDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.CoinsDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.HistoryDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.SettingsDialog;
import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.HistoryElement;
import com.kaasbrot.boehlersyugiohapp.history.HistoryElementParser;
import com.kaasbrot.boehlersyugiohapp.history.NewGame;
import com.kaasbrot.boehlersyugiohapp.history.Points;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements ButtonDeterminer {

    MenuItem redoButton;
    MenuItem undoButton;
    MenuItem timerShowButton;

    int currentMenu;

    int screen_height;
    int screen_width;
    int screen_ratio;
    int screen_height_sp;
    int screen_width_sp;

    int actionbar_height;
    int actionbar_width;
    int lifetextsize;
    int numberbuttontextsize;
    int timertextsize;


    Toolbar toolbar;

    EditText timerText;

    // counts seconds passed since game started
    GameTimer gameTimer;

    HistoryDialog historyDialog;
    CasinoDialog casinoDialog;
    CoinDialog coinDialog;
    CoinsDialog coinsDialog;
    SettingsDialog settingsDialog;
    TextView belowtimertext;
    int belowtimersize;

    CooldownTracker cooldowns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load history
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        GlobalOptions.setPrefs(sharedPreferences);

        String json = sharedPreferences.getString(GlobalOptions.HISTORY, "");
        history = new History(8000, 8000);
        if(!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<HistoryElementParser>>(){}.getType();

            HistoryElementParser.prev = history.getLastPoints();
            ArrayList<HistoryElementParser> elements = new Gson().fromJson(json, listType);
            elements.remove(0);
            elements.forEach(el -> history.add(el.parse()));
        }
        history.setEditor(sharedPreferences.edit());
        // hopefully done with loading

        cooldowns = new CooldownTracker();

        getWindow().setNavigationBarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary,null));
        setContentView(GlobalOptions.getCurrentView().layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(GlobalOptions.isFirstView()) {
            currentMenu = R.menu.menu_main;
            toolbar = findViewById(R.id.toolbar_main);
            belowtimertext = findViewById(R.id.TextBelow);
            belowtimertext.setTextSize(belowtimersize);
        } else {
            currentMenu = R.menu.menu_points;
            toolbar = findViewById(R.id.toolbar_points);
        }

        setSupportActionBar(toolbar);

        gameTimer = new GameTimer();
        updateComponentActivities();

        historyDialog = new HistoryDialog();
        historyDialog.setHistory(history);

        casinoDialog = new CasinoDialog();
        casinoDialog.setHistory(history);

        coinDialog = new CoinDialog();
        coinDialog.setHistory(history);

        coinsDialog = new CoinsDialog();
        coinsDialog.setHistory(history);

        settingsDialog = new SettingsDialog();

        getScreenSize();
        getActionBarHeight();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Points p = history.getLastPoints();
        p1.reset(p.p1);
        p2.reset(p.p2);
    }

    public void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point m_size = new Point();
        display.getSize(m_size);
        screen_width = m_size.x;
        screen_height = m_size.y;
        screen_ratio = screen_height/screen_width;
        screen_width_sp = (int)(screen_width / getResources().getDisplayMetrics().scaledDensity);
        screen_height_sp = (int)(screen_height / getResources().getDisplayMetrics().scaledDensity);

        belowtimersize = (screen_ratio > 2) ? 30 : 0;

        if(screen_height_sp > 650){
            //toggletimermax=60;
            //toggletimermax = 200;
            gameTimer.setTopMarginMax(190);
            lifetextsize=34;
            numberbuttontextsize=32;
            timertextsize=24;
            GlobalOptions.settingstextsize=20;
        }else if(screen_height_sp > 580){
            //toggletimermax=55;
            //toggletimermax = 183;
            gameTimer.setTopMarginMax(175);
            lifetextsize=22;
            numberbuttontextsize=26;
            timertextsize=20;
            GlobalOptions.settingstextsize=16;
        }else{
            //toggletimermax=50;
            //toggletimermax = 167;
            gameTimer.setTopMarginMax(167);
            lifetextsize=20;
            numberbuttontextsize=20;
            timertextsize=18;
            GlobalOptions.settingstextsize=14;
        }
    }

    public void getActionBarHeight() {
        actionbar_height = toolbar.getLayoutParams().height;
        actionbar_width = toolbar.getLayoutParams().width;

    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
        //pixelsToSp(getActivity(), number);
    }

    public void AdjustToScreen() {
        ((EditText) findViewById(R.id.timerText)).setTextSize(timertextsize);
        if(GlobalOptions.isFirstView()) {
            ((TextView) findViewById(R.id.pointsPlayer1)).setTextSize(lifetextsize);
            ((TextView) findViewById(R.id.pointsPlayer2)).setTextSize(lifetextsize);
        } else {
            ((TextView) findViewById(R.id.pointsPlayer1)).setTextSize(lifetextsize);
            ((TextView) findViewById(R.id.pointsPlayer2)).setTextSize(lifetextsize);
            ((TextView) findViewById(R.id.customInput)).setTextSize(lifetextsize);
            ((Button) findViewById(R.id.button00)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button0)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button1)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button2)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button3)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button4)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button5)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button6)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button7)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button8)).setTextSize(numberbuttontextsize);
            ((Button) findViewById(R.id.button9)).setTextSize(numberbuttontextsize);
        }

    }

    public void toggleScreenAlwaysOn(View v) {
        if(!cooldowns.tryAndStartTracker("screenOn"))
            return;

        settingsDialog.toggleScreenAlwaysOn();
        if(GlobalOptions.isScreenAlwaysOn()) {
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void toggleDeleteHistory(View v) {
        if(!cooldowns.tryAndStartTracker("deleteHistory"))
            return;

        settingsDialog.toggleDeleteAfter4Games();
        if(!GlobalOptions.isDeleteAfter4()) {
            history.removeNewGamesExcept4();
        }
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
        gameTimer.updateActivity(findViewById(R.id.viewTimer));


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
            gameTimer.updateTimerText();
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
                gameTimer.updateTimerText();
                return;
            }
        }

        // no more than 5 hours!
        if (seconds > 5 * 60 * 60) {
            toast.setText(R.string.timer_err_max_exceeded);
            toast.show();
            gameTimer.updateTimerText();
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
        timerShowButton.setTitle(gameTimer.isTimerVisible() ? R.string.hide_timer : R.string.show_timer);

        MenuItem timerButton = menu.findItem(R.id.action_start_timer);
        if(gameTimer.isTimerVisible()) {
            timerButton.setTitle(R.string.hide_timer);
        } else {
            timerButton.setTitle(R.string.show_timer);
        }

        // clock from here: https://stackoverflow.com/questions/5437674/what-unicode-characters-represent-time
        // setTitle("⏰");

        // check if undo/redo Buttons should be enabled
        // determineButtonEnable();
        if(screen_width_sp < 380) {
            menu.getItem(6).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        if(screen_width_sp < 320) {
            menu.getItem(3).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        AdjustToScreen();
        return true;
    }

    /**
     * Since Toolbar buttons directly call methods, this can be ignored.
     * I just left it here IN CASE! (you never know)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();
        if (item.getItemId() == R.id.action_show_casino) {
            showCasino();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check if undo / redo buttons should be enabled.
     * @param menu Menu bar
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
        new Handler().postDelayed(() -> inputField.setText("0"),200);


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
                inputField.setText(String.valueOf(newAmount));

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
            new Handler().postDelayed(() -> inputField.setText("0"),200);

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
     */
    public void reset(MenuItem item) {
        p1.reset();
        p2.reset();
        //Dialog "Neues Duell" hinzufügen

        List<HistoryElement> h = history.getHistory();
        if(h.size() <= 1 || h.get(h.size()-2) instanceof NewGame)
            return;

        history.add(new NewGame());
        history.add(p1.points, p2.points);
        if(GlobalOptions.isDeleteAfter4()){
            history.removeNewGamesExcept4();
        }
    }

    /**
     * Undo or Redo button pressed -> update player's points text field
     */
    private void historyAction(Points newPoints) {
        p1.points = newPoints.p1;
        p2.points = newPoints.p2;

        p1.updatePointsText();
        p2.updatePointsText();

        determineButtonEnable();
    }

    /**
     * Called from Toolbar
     * Undo last action. If undo's not possible (eg. at the beginning), nothing happens.
     */
    public void undo(MenuItem item) {
        cancelTimers();
        historyAction(history.undo());
    }

    /**
     * Called from Toolbar
     * Redo last action (after undo). If redo's not possible, nothing happens.
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
        if(!cooldowns.tryAndStartTracker("toggleTimer"))
            return;

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
        if(!cooldowns.tryAndStartTracker("settings"))
            return;

        settingsDialog.show(getSupportFragmentManager(), "Settings");
    }

    @Override
    public void onBackPressed(){
        //clear focus with double back
        if(timerText != null) {timerText.clearFocus();}
    }


    public void threecoins(MenuItem item) {
        //historyDialog.show(getSupportFragmentManager(), "History Dialog");
        coinsDialog.show(getSupportFragmentManager(), "Coins");
    }

    public void showCasino() {
        if(!cooldowns.tryAndStartTracker("casino"))
            return;

        casinoDialog.show(getSupportFragmentManager(), "Casino");
    }

    public void showCoin(MenuItem item) {
        if(!cooldowns.tryAndStartTracker("coin"))
            return;

        coinDialog.show(getSupportFragmentManager(), "Coin");
    }
    // Equipment for drag queens
    private int y1, y2;
    static final int MIN_DISTANCE = 50;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int deltaY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                y2 = (int) event.getY();
                deltaY = y2 - y1;
                if (Math.abs(deltaY) > MIN_DISTANCE) {


                    if (deltaY > 0 && !gameTimer.isTimerVisible()) {
                        // down gesture, show timer
                       toggleTimerVisibility(timerShowButton);
                    } else if (deltaY < 0 && gameTimer.isTimerVisible()) {
                        // up gesture, hide timer
                       toggleTimerVisibility(timerShowButton);
                    }}
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Check if undo, redo buttons have to be enabled or not.
     * By invalidating the options menu, {@link #onPrepareOptionsMenu(Menu)} is called.
     */
    public void determineButtonEnable() {
        runOnUiThread(this::invalidateOptionsMenu);
    }

    /**
     * Called from Toolbar
     * Show game history in dialog
     *
     * @param item Menu Item
     */
    public void showHistory(MenuItem item) {
        //if cooldown on, do nothing, else set cooldown, then unset after x time
        if(!cooldowns.tryAndStartTracker("history"))
            return;

        historyDialog.show(getSupportFragmentManager(), "History Dialog");
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
        if(GlobalOptions.isFirstView()) {
            setContentView(GlobalOptions.Views.SECOND_VIEW.layout);

            toolbar_id = R.id.toolbar_points;
            currentMenu = R.menu.menu_points;
            GlobalOptions.setCurrentView(GlobalOptions.Views.SECOND_VIEW);

        } else {
            setContentView(GlobalOptions.Views.FIRST_VIEW.layout);

            toolbar_id = R.id.toolbar_main;
            currentMenu = R.menu.menu_main;
            GlobalOptions.setCurrentView(GlobalOptions.Views.FIRST_VIEW);
        }

        toolbar = findViewById(toolbar_id);
        setSupportActionBar(toolbar);
        AdjustToScreen();
        updateComponentActivities();

        if(GlobalOptions.isFirstView()) {
            belowtimertext = findViewById(R.id.TextBelow);
            belowtimertext.setTextSize(belowtimersize);
        }
    }

}
