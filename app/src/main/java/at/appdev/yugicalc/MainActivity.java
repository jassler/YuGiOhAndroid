package at.appdev.yugicalc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.os.LocaleListCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.stream.Collectors;

import at.appdev.yugicalc.dialog.AboutDialog;
import at.appdev.yugicalc.dialog.CasinoDialog;
import at.appdev.yugicalc.dialog.CoinDialog;
import at.appdev.yugicalc.dialog.CoinsDialog;
import at.appdev.yugicalc.dialog.HistoryDialog;
import at.appdev.yugicalc.dialog.SettingsDialog;
import at.appdev.yugicalc.history.History;
import at.appdev.yugicalc.history.HistoryElementParser;
import at.appdev.yugicalc.history.Points;

public class MainActivity extends AppCompatActivity implements ButtonDeterminer {

    MenuItem redoButton;
    MenuItem undoButton;
    MenuItem timerShowButton;

    int currentMenu;

    int screenHeight;
    int screenWidth;
    int screenRatio;
    int screenHeightSp;
    int screenWidthSp;

    int actionbarHeight;
    int actionbarWidth;
    int emptyTextSize;
    int playernameTextSize;
    int lifeTextSize;
    int numberButtonTextSize;
    int timerTextSize;

    Toolbar toolbar;

    EditText timerText;

    // counts seconds passed since game started
    GameTimer gameTimer;

    HistoryDialog historyDialog;
    CasinoDialog casinoDialog;
    CoinDialog coinDialog;
    CoinsDialog coinsDialog;
    SettingsDialog settingsDialog;
    AboutDialog aboutDialog;
    int belowTimerSize;

    CooldownTracker cooldowns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load history
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        String json = sharedPreferences.getString(GlobalOptions.HISTORY, "");

        if(json.isEmpty()) {
            GameInformation.history = new History(new Points(GlobalOptions.getStartingLifePoints(), GlobalOptions.getStartingLifePoints(), true));
        } else {
            Type listType = new TypeToken<ArrayList<HistoryElementParser>>(){}.getType();
            ArrayList<HistoryElementParser> elements = new Gson().fromJson(json, listType);
            GameInformation.history = new History(
                    elements.stream().map(HistoryElementParser::parse).collect(Collectors.toList())
            );
        }
        GameInformation.history.setEditor(sharedPreferences.edit());

        GlobalOptions.setPrefs(sharedPreferences);
        String[] names = GameInformation.history.getCurrentPoints().getNames();
        GlobalOptions.setPlayerName1(names[0]);
        GlobalOptions.setPlayerName2(names[1]);
        // hopefully done with loading
        cooldowns = new CooldownTracker();


        //LANGUAGE STUFF
        String loadedlanguage = GlobalOptions.getLanguage();
        if(loadedlanguage.equals("en")) {
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(loadedlanguage);
            AppCompatDelegate.setApplicationLocales(appLocale);
        }else if(loadedlanguage.equals("de")){
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(loadedlanguage);
            AppCompatDelegate.setApplicationLocales(appLocale);
        }


        getWindow().setNavigationBarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary,null));
        setContentView(GlobalOptions.getCurrentView().layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(GlobalOptions.isFirstView()) {
            currentMenu = R.menu.menu_main;
            toolbar = findViewById(R.id.toolbar_main);
            TextView tv = findViewById(R.id.TextBelow);
            tv.setTextSize(belowTimerSize);
        } else {
            currentMenu = R.menu.menu_points;
            toolbar = findViewById(R.id.toolbar_points);
        }

        setSupportActionBar(toolbar);

        gameTimer = new GameTimer();
        updateComponentActivities();

        historyDialog = new HistoryDialog(GameInformation.history);
        historyDialog.setActivity(this);

        casinoDialog = new CasinoDialog();
        casinoDialog.setHistory(GameInformation.history);

        coinDialog = new CoinDialog();
        coinDialog.setHistory(GameInformation.history);

        coinsDialog = new CoinsDialog();
        coinsDialog.setHistory(GameInformation.history);

        settingsDialog = new SettingsDialog();
        settingsDialog.setMainActivity(this);

        aboutDialog = new AboutDialog();

        initScreenSize();
        initActionBarHeight();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Points p = GameInformation.history.getCurrentPoints();
        GameInformation.p1.reset(p.p1);
        GameInformation.p2.reset(p.p2);
        adjustToScreen();
    }

    public void initScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point m_size = new Point();
        display.getSize(m_size);
        screenWidth = m_size.x;
        screenHeight = m_size.y;
        screenRatio = screenHeight / screenWidth;
        screenWidthSp = (int)(screenWidth / getResources().getDisplayMetrics().scaledDensity);
        screenHeightSp = (int)(screenHeight / getResources().getDisplayMetrics().scaledDensity);

        belowTimerSize = (screenRatio > 2) ? 30 : 0;

        if(screenHeightSp > 650){
            //toggletimermax=60;
            //toggletimermax = 200;
            gameTimer.setTopMarginMax(200);
            lifeTextSize =34;
            playernameTextSize =18;
            numberButtonTextSize =32;
            timerTextSize =24;
            emptyTextSize =10;
            GlobalOptions.settingstextsize=20;
            GlobalOptions.fakeswitchimagesize=120;
        }else if(screenHeightSp > 580){
            //toggletimermax=55;
            //toggletimermax = 183;
            gameTimer.setTopMarginMax(135);
            lifeTextSize =22;
            playernameTextSize =15;
            numberButtonTextSize =26;
            timerTextSize =20;
            emptyTextSize =6;
            GlobalOptions.settingstextsize=16;
            GlobalOptions.fakeswitchimagesize=75;
        }else{
            //toggletimermax=50;
            //toggletimermax = 167;
            gameTimer.setTopMarginMax(90);
            lifeTextSize =20;
            playernameTextSize =12;
            numberButtonTextSize =20;
            timerTextSize =18;
            emptyTextSize =2;
            GlobalOptions.settingstextsize=14;
            GlobalOptions.fakeswitchimagesize=50;
        }
    }

    public void initActionBarHeight() {
        actionbarHeight = toolbar.getLayoutParams().height;
        actionbarWidth = toolbar.getLayoutParams().width;

    }

//    public static float pixelsToSp(Context context, float px) {
//        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
//        return px/scaledDensity;
//        //pixelsToSp(getActivity(), number);
//    }

    public void adjustToScreen() {
        ((EditText) findViewById(R.id.timerText)).setTextSize(timerTextSize);
        if(GlobalOptions.isFirstView()) {
            ((TextView) findViewById(R.id.pointsPlayer1)).setTextSize(lifeTextSize);
            ((TextView) findViewById(R.id.pointsPlayer2)).setTextSize(lifeTextSize);
            ((TextView) findViewById(R.id.emptytext1)).setTextSize(emptyTextSize);
            ((TextView) findViewById(R.id.emptytext2)).setTextSize(emptyTextSize);
        } else {
            ((TextView) findViewById(R.id.pointsPlayer1)).setTextSize(lifeTextSize);
            ((TextView) findViewById(R.id.pointsPlayer2)).setTextSize(lifeTextSize);
            ((TextView) findViewById(R.id.customInput)).setTextSize(lifeTextSize);
            ((Button) findViewById(R.id.button00)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button0)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button1)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button2)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button3)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button4)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button5)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button6)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button7)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button8)).setTextSize(numberButtonTextSize);
            ((Button) findViewById(R.id.button9)).setTextSize(numberButtonTextSize);
        }

        if(GlobalOptions.isShowNames()){
            ((TextView) findViewById(R.id.namePlayer1)).setTextSize(playernameTextSize);
            ((TextView) findViewById(R.id.namePlayer2)).setTextSize(playernameTextSize);
        } else {
            ((TextView) findViewById(R.id.namePlayer1)).setTextSize(0);
            ((TextView) findViewById(R.id.namePlayer2)).setTextSize(0);
        }

        ((TextView) findViewById(R.id.pointsPlayer1)).setTextSize(lifeTextSize);
        ((TextView) findViewById(R.id.pointsPlayer2)).setTextSize(lifeTextSize);

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
            GameInformation.history.removeNewGamesExcept4();
        }
    }

    public void toggleShowNames(View v) {
        if(!cooldowns.tryAndStartTracker("showNames"))
            return;

        settingsDialog.toggleShowNames();
        adjustToScreen();
    }

    /**
     * When loading view for the first time or switching views, make sure
     * all components points to the correct view elements on screen.
     */
    private void updateComponentActivities() {
        GameInformation.p1.updateActivity(
                findViewById(R.id.pointsPlayer1),
                findViewById(R.id.tmpText1),
                this
        );
        GameInformation.p2.updateActivity(
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
        updatePlayerNames();

    }

    public void updatePlayerNames() {
        String p1Name = GlobalOptions.getPlayerName1();
        String p2Name = GlobalOptions.getPlayerName2();

        TextView name = findViewById(R.id.namePlayer1);
        if(p1Name.isEmpty()) {
            name.setText(R.string.playername1);
        } else {
            name.setText(p1Name);
        }

        name = findViewById(R.id.namePlayer2);
        if(p2Name.isEmpty()) {
            name.setText(R.string.playername2);
        } else {
            name.setText(p2Name);
        }

        if(GlobalOptions.isShowNames()) {
            GameInformation.history.updateNames(p1Name, p2Name);
            adjustToScreen();
        } else {
            GameInformation.history.updateNames("","");
            adjustToScreen();
        }
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
        if(screenWidthSp < 380) {
            menu.getItem(6).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        if(screenWidthSp < 320) {
            menu.getItem(3).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        //AdjustToScreen();
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

        if(GameInformation.history.canUndo()) {
            undoButton.setEnabled(true);
            undoButton.getIcon().setAlpha(255);
        } else {
            undoButton.setEnabled(false);
            undoButton.getIcon().setAlpha(50);
        }

        if(GameInformation.history.canRedo()) {
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
            return GameInformation.p1;
        } else {
            return GameInformation.p2;
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

    public void cancelWait() {
        GameInformation.p1.calculate(0,false);
        GameInformation.p2.calculate(0,false);
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
        GameInformation.p1.cancelTimer();
        GameInformation.p2.cancelTimer();
    }

    /**
     * Called from Toolbar
     * Cancel timers, animations. Reset points.
     */
    public void reset(MenuItem item) {
        GameInformation.p1.reset();
        GameInformation.p2.reset();
        //Dialog "Neues Duell" hinzufügen

        GameInformation.history.addNewGame();
        if(GlobalOptions.isDeleteAfter4()){
            GameInformation.history.removeNewGamesExcept4();
        }
    }

    /**
     * Undo or Redo button pressed -> update player's points text field
     */
    private void historyAction(Points newPoints) {
        GameInformation.p1.points = newPoints.p1;
        GameInformation.p2.points = newPoints.p2;

        GameInformation.p1.updatePointsText();
        GameInformation.p2.updatePointsText();

        determineButtonEnable();

        String[] names = newPoints.getNames();
        GlobalOptions.setPlayerName1(names[0]);
        GlobalOptions.setPlayerName2(names[1]);
        updatePlayerNames();
    }

    /**
     * Called from Toolbar
     * Undo last action. If undo's not possible (eg. at the beginning), nothing happens.
     */
    public void undo(MenuItem item) {
        cancelTimers();
        historyAction(GameInformation.history.undo());
    }

    /**
     * Called from Toolbar
     * Redo last action (after undo). If redo's not possible, nothing happens.
     */
    public void redo(MenuItem item) {
        cancelTimers();
        historyAction(GameInformation.history.redo());
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
        if(settingsDialog.isAdded())
            return;

        settingsDialog.show(getSupportFragmentManager(), "Settings");
    }

    public void showAboutUs(MenuItem item) {
        if(aboutDialog.isAdded())
            return;

        aboutDialog.show(getSupportFragmentManager(), "AboutUs");
    }

    @Override
    public void onBackPressed(){
        //clear focus with double back
        if(timerText != null) {timerText.clearFocus();}
    }


    public void threecoins(MenuItem item) {
        if(coinsDialog.isAdded())
            return;

        coinsDialog.show(getSupportFragmentManager(), "Coins");
    }

    public void showCasino() {
        if(casinoDialog.isAdded())
            return;

        casinoDialog.show(getSupportFragmentManager(), "Casino");
    }

    public void showCoin(MenuItem item) {
        if(coinDialog.isAdded())
            return;

        coinDialog.show(getSupportFragmentManager(), "Coin");
    }
    // Equipment for drag queens
    private int y1, y2;
    static final int MIN_DISTANCE = 50;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int deltaY;
        cancelWait();
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
        // make sure not to accidentally generate the historyDialog twice
        // else we might get some weird FragmentException
        if(historyDialog.isAdded())
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
        adjustToScreen();
        updateComponentActivities();

        if(GlobalOptions.isFirstView()) {
            TextView tv = findViewById(R.id.TextBelow);
            tv.setTextSize(belowTimerSize);
        }
    }

}
