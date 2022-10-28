package com.kaasbrot.boehlersyugiohapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import android.os.*; //and this

import static com.kaasbrot.boehlersyugiohapp.GameInformation.history;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p1;
import static com.kaasbrot.boehlersyugiohapp.GameInformation.p2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaasbrot.boehlersyugiohapp.dialog.CoinDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.CoinsDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.HistoryDialog;
import com.kaasbrot.boehlersyugiohapp.dialog.CasinoDialog;
import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.HistoryElement;
import com.kaasbrot.boehlersyugiohapp.history.HistoryElementParser;
import com.kaasbrot.boehlersyugiohapp.history.NewGame;
import com.kaasbrot.boehlersyugiohapp.history.Points;

public class MainActivity extends AppCompatActivity implements ButtonDeterminer {

    MenuItem redoButton;
    MenuItem undoButton;
    MenuItem timerShowButton;

    int currentContentView;
    int currentMenu;

    int screen_height;
    int screen_width;
    int screen_ratio;
    int screen_height_sp;
    int screen_width_sp;

    int toggletimermax;
    int toggletimerfrequency;
    int actionbar_height;
    int actionbar_width;
    int lifetextsize;
    int numberbuttontextsize;
    int timertextsize;
    int settingstextsize;

    //data that is stored locally
    int startinglifepoints;
    int keepscreenon;
    int deleteafter4;
    int rememberview;


    private Menu mOptionsMenu;

    Toolbar toolbar;

    EditText timerText;

    // counts seconds passed since game started
    GameTimer gameTimer;
    Random rand;

    HistoryDialog historyDialog;
    CasinoDialog casinoDialog;
    CoinDialog coinDialog;
    CoinsDialog coinsDialog;
    TextView abovetimertext;
    TextView belowtimertext;
    int abovetimersize = 1;
    int belowtimersize;

    SharedPreferences.Editor edit; //shortcut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load history
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        edit = sharedPreferences.edit();
        startinglifepoints = sharedPreferences.getInt("startinglifepoints",8000);
        keepscreenon = sharedPreferences.getInt("keepscreenon",1);
        deleteafter4 = sharedPreferences.getInt("deleteafter4",1);
        rememberview = sharedPreferences.getInt("rememberview",1);

        String json = sharedPreferences.getString("history", "");
        history = new History(8000, 8000);
        if(!json.isEmpty()) {
            Type listType = new TypeToken<ArrayList<HistoryElementParser>>(){}.getType();

            HistoryElementParser.prev = history.getLastPoints();
            ArrayList<HistoryElementParser> elements = new Gson().fromJson(json, listType);
            elements.remove(0);
            elements.forEach(el -> history.add(el.parse()));
        }
        history.setEditor(edit);
        // hopefully done with loading

        if(rememberview==1){
        currentContentView = R.layout.activity_main;
        }else{
        currentContentView = R.layout.activity_points;
        }
        getWindow().setNavigationBarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary,null));
        setContentView(currentContentView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(rememberview==1){
            currentMenu = R.menu.menu_main;
            toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        }else{
            currentMenu = R.menu.menu_points;
            toolbar = (Toolbar) findViewById(R.id.toolbar_points);
        }



        setSupportActionBar(toolbar);

        gameTimer = new GameTimer();
        rand = new Random();
        updateComponentActivities();

        historyDialog = new HistoryDialog();
        historyDialog.setHistory(history);

        casinoDialog = new CasinoDialog();
        casinoDialog.setHistory(history);

        coinDialog = new CoinDialog();
        coinDialog.setHistory(history);

        coinsDialog = new CoinsDialog();
        coinsDialog.setHistory(history);

        getScreenSize();
        getActionBarHeight();

        abovetimertext = findViewById(R.id.AboveTimer);
        abovetimertext.setTextSize(abovetimersize);
        if(rememberview==1){
        belowtimertext = findViewById(R.id.TextBelow);
        belowtimertext.setTextSize(belowtimersize);}


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

        if(screen_ratio > 2){belowtimersize = 30;} else {belowtimersize = 0;}
        if(screen_height_sp > 650){
            toggletimermax=60;
            toggletimerfrequency = 30;
            lifetextsize=30;
            numberbuttontextsize=32;
            timertextsize=24;
            settingstextsize=20;
        }else{if(screen_height_sp > 580){
            toggletimermax=55;
            toggletimerfrequency = 20;
            lifetextsize=20;
            numberbuttontextsize=26;
            timertextsize=20;
            settingstextsize=16;
        }else{
            toggletimermax=50;
            toggletimerfrequency = 12;
            lifetextsize=20;
            numberbuttontextsize=20;
            timertextsize=18;
            settingstextsize=14;
        }}
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
        if(currentContentView == R.layout.activity_main) {
            ((TextView) findViewById(R.id.pointsPlayer1)).setTextSize(lifetextsize);
            ((TextView) findViewById(R.id.pointsPlayer2)).setTextSize(lifetextsize);
        }
        else{
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
    private int toggleScreenOnCooldown = 0;
    public void toggleScreenAlwaysOn(View v) {
        ImageView buttonimage = v.findViewById(R.id.tickbutton1);
        if (toggleScreenOnCooldown == 1) {
        } else {
            toggleScreenOnCooldown = 1;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleScreenOnCooldown = 0;
                }
            }, 500);
        if(keepscreenon==0){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            keepscreenon=1;
            buttonimage.setImageResource(R.drawable.tick1);
        }else{
            getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            keepscreenon=0;
            buttonimage.setImageResource(R.drawable.tick0);
        }
        edit.putInt("keepscreenon",keepscreenon);
        edit.apply();
    }}

    private int toggleDeleteHistoryCooldown = 0;
    public void toggleDeleteHistory(View v) {
        ImageView buttonimage = v.findViewById(R.id.tickbutton2);
        if (toggleDeleteHistoryCooldown == 1) {
        } else {
            toggleDeleteHistoryCooldown = 1;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleDeleteHistoryCooldown = 0;
                }
            }, 500);
        if(deleteafter4==0){
            deleteafter4=1;
            history.removeNewGamesExcept4();
            buttonimage.setImageResource(R.drawable.tick1);
        }else{
            deleteafter4=0;
            buttonimage.setImageResource(R.drawable.tick0);
        }
        edit.putInt("deleteafter4",deleteafter4);
        edit.apply();
    }}
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
        // determineButtonEnable();
        mOptionsMenu = menu;
        if(screen_width_sp < 380) {
            mOptionsMenu.getItem(6).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        if(screen_width_sp < 320) {
            mOptionsMenu.getItem(3).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        AdjustToScreen();
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
            switch (item.getItemId()) {
                case R.id.action_show_casino:
                    showCasino();
                    return true;
            }
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
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    inputField.setText("0");
                }
            },200);

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
        p1.reset(startinglifepoints);
        p2.reset(startinglifepoints);
        //Dialog "Neues Duell" hinzufügen

        List<HistoryElement> h = history.getHistory();
        if(h.size() <= 1 || h.get(h.size()-2) instanceof NewGame)
            return;

        history.add(new NewGame());
        history.add(p1.points, p2.points);
        if(deleteafter4==1){
        history.removeNewGamesExcept4();
        }
    }

    /**
     * Undo or Redo button pressed -> update player's points text field
     * @param newPoints
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

    private int toggletimercooldown;
    private int toggletimermin = 0;
    //private int toggletimermax = 60; //defined in getScreenSize
    private int toggletimertime = 480;
    // private int toggletimerfrequency = 15; //defined in getScreenSize
    /**
     * Called from Toolbar
     * If timer is running, stop and hide timer.
     * If timer is not running, start and update timer display every second.
     * @param item Timer menu item. Text is updated whether timer is shown or not.
     */
    public void toggleTimerVisibility(MenuItem item) {
        if (toggletimercooldown == 1) {
        } else {
            toggletimercooldown = 1;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggletimercooldown = 0;
                }
            }, 500);

            if (gameTimer.isTimerVisible()) {
                clearTimerTextFocus();
                for (int i = 1; i < toggletimerfrequency; i++){
                    int finalI = i;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            abovetimersize = toggletimermax-(toggletimermax-toggletimermin)/(toggletimerfrequency)* finalI;
                            abovetimertext.setTextSize(abovetimersize);
                        }
                    }, (toggletimertime/toggletimerfrequency)*finalI);

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abovetimertext.setTextSize(toggletimermin);
                    }
                },toggletimertime);
        } else {
            abovetimersize = toggletimermin;
            for (int i = 1; i < toggletimerfrequency; i++){
                int finalI = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abovetimersize = toggletimermin+(toggletimermax-toggletimermin)/(toggletimerfrequency)* finalI;
                        abovetimertext.setTextSize(abovetimersize);
                    }
                }, (toggletimertime/toggletimerfrequency)*i);
        }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abovetimertext.setTextSize(toggletimermax);
                    }
                },toggletimertime);
        }
            gameTimer.toggleTimerVisibility(item);
    }}

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
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.settings_dialogr);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        dialog.getWindow().getAttributes().windowAnimations = R.style.MyDialogTheme;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView okay_text = dialog.findViewById(R.id.settingsok);
        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.settingsok)).setTextSize(settingstextsize);
        ((TextView) dialog.findViewById(R.id.StartLifeText)).setTextSize(settingstextsize);
        ((TextView) dialog.findViewById(R.id.KeepScreenOnText)).setTextSize(settingstextsize);
        ((TextView) dialog.findViewById(R.id.KeepHistoryText)).setTextSize(settingstextsize);
        ((TextView) dialog.findViewById(R.id.BehindEdit)).setTextSize(settingstextsize);
        ((EditText) dialog.findViewById(R.id.StartLifeInput)).setTextSize(settingstextsize);

        EditText startlifetext = dialog.findViewById(R.id.StartLifeInput);
        startlifetext.setText(String.valueOf(startinglifepoints), TextView.BufferType.EDITABLE);
        startlifetext.setSelectAllOnFocus(true);
        startlifetext.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                startlifetext.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(startlifetext.getApplicationWindowToken(), 0);
                String startlifetemp = startlifetext.getText().toString();
                int startlifetempint = Integer.parseInt(startlifetemp);
                Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 10);
                if(startlifetempint>40000){
                    toast.setText(R.string.set_lifepoint_max);
                    toast.show();
                    return true;
                } else {
                    startinglifepoints = startlifetempint;
                    edit.putInt("startinglifepoints",startinglifepoints);
                    edit.apply();
                }
            }

            return true;
        });
        ImageView buttonimage1 = dialog.findViewById(R.id.tickbutton1);
        if(keepscreenon==0){
            buttonimage1.setImageResource(R.drawable.tick0);
        }else{
            buttonimage1.setImageResource(R.drawable.tick1);
        }
        ImageView buttonimage2 = dialog.findViewById(R.id.tickbutton2);
        if(deleteafter4==0){
            buttonimage2.setImageResource(R.drawable.tick0);
        }else{
            buttonimage2.setImageResource(R.drawable.tick1);
        }

        dialog.show();
    }
    public void threecoins(MenuItem item) {
        //historyDialog.show(getSupportFragmentManager(), "History Dialog");
        coinsDialog.show(getSupportFragmentManager(), "Coins");
    }

    //cooldown for dice
    private int dicecooldown = 0;

    public void showCasino() {
        if(dicecooldown==1) { } else
        {
            dicecooldown=1;
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    dicecooldown=0;
                }
            },1500);
        //actual Casino
        casinoDialog.show(getSupportFragmentManager(), "Casino");}
    }

    private int coincooldown = 0;

    public void showCoin(MenuItem item) {
            if(coincooldown==1) { } else
            {
                coincooldown=1;
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        coincooldown=0;
                    }
                },1500);
        //actual Coin
        coinDialog.show(getSupportFragmentManager(), "Coin");}
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

        /*int deltaY;
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
        }*/ //old drag down stuff
        return super.onTouchEvent(event);
    }

    /**
     * Check if undo, redo buttons have to be enabled or not.
     * By invalidating the options menu, {@link #onPrepareOptionsMenu(Menu)} is called.
     */
    public void determineButtonEnable() {
        runOnUiThread(this::invalidateOptionsMenu);
    }

    //cooldown variable for history button overflow
    private int historycooldown = 0;

    /**
     * Called from Toolbar
     * Show game history in dialog
     *
     * @param item Menu Item
     */
    public void showHistory(MenuItem item) {
        //if cooldown on, do nothing, else set cooldown, then unset after x time

        if(historycooldown==1) { } else
        {
            historycooldown=1;
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    historycooldown=0;
                }
            },1500);
            historyDialog.show(getSupportFragmentManager(), "History Dialog");
        }
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
            rememberview = 2;
        } else {
            currentContentView = R.layout.activity_main;
            toolbar_id = R.id.toolbar_main;
            currentMenu = R.menu.menu_main;
            rememberview = 1;
        }
        setContentView(currentContentView);

        if(currentContentView == R.layout.activity_main){
            belowtimertext = findViewById(R.id.TextBelow);
            belowtimertext.setTextSize(belowtimersize);}

        toolbar = findViewById(toolbar_id);
        setSupportActionBar(toolbar);
        abovetimertext = findViewById(R.id.AboveTimer);
        abovetimertext.setTextSize(abovetimersize);
        edit.putInt("rememberview",rememberview);
        edit.apply();
        AdjustToScreen();
        updateComponentActivities();
    }

}
