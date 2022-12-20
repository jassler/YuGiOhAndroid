package at.appdev.yugicalc;

import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.TextView;

import static at.appdev.yugicalc.GlobalOptions.isTimerRunning;
import static at.appdev.yugicalc.GlobalOptions.getTimerPauseTime;
import static at.appdev.yugicalc.GlobalOptions.getTimerStartTime;
import static at.appdev.yugicalc.GlobalOptions.setTimerValues;
import static at.appdev.yugicalc.GlobalOptions.setTimerVisible;


import java.util.Locale;

public class GameTimer {

    public static final long TIME_LIMIT = 9 * 60 * 60 + 59 * 60 + 59;

    private final Handler handler;

    private final ReverseInterpolator interpolator;
    private final Animation animation;
    private int topMarginMax = 650;

    private View viewTimer;
    private TextView textTimer;
    private TextView topBox;
    private ImageButton startStopButton;
    private ConstraintLayout.LayoutParams layoutParams;

    private final Runnable repeatingCall = new Runnable() {
        @Override
        public void run() {
            updateTimerText();

            // call function again in a quarter of a second second
            handler.postDelayed(repeatingCall, 250);
        }
    };

    public GameTimer() {
        this.handler = new Handler();
        this.animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                layoutParams.topMargin = (int) (topMarginMax * interpolatedTime);
                viewTimer.requestLayout();
            }
        };
        this.interpolator = new ReverseInterpolator(new AccelerateDecelerateInterpolator(), false);
        this.animation.setDuration(500);
        this.animation.setInterpolator(interpolator);

        if(getSecondsPassed() > TIME_LIMIT) {
            // maybe timer was left running overnight by accident
            setTimerValues(false, 0, 0);
        }

        if(isRunning()) {
            repeatingCall.run();
        }
    }

    public void setTopMarginMax(int max) {
        this.topMarginMax = max;
        if(isTimerVisible()) {
            this.layoutParams.topMargin = max;
            this.viewTimer.requestLayout();
        }
    }

    public void updateActivity(View viewTimer) {
        this.viewTimer = viewTimer;
        this.textTimer = viewTimer.findViewById(R.id.timerText);
        this.topBox = viewTimer.findViewById(R.id.AboveTimer);
        this.startStopButton = viewTimer.findViewById(R.id.timerPlayPauseButton);
        this.layoutParams = (ConstraintLayout.LayoutParams) viewTimer.getLayoutParams();

        this.layoutParams.topMargin = isTimerVisible() ? topMarginMax : 0;
        viewTimer.requestLayout();

        updateTimerText();
        updateButtonImage();
    }

    public long getSecondsPassed() {
        long tmp = getTimerPauseTime();
        if (tmp >= 1) {
            return tmp / 1000;
        }
        tmp = getTimerStartTime();
        if (tmp == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - tmp) / 1000;
    }

    public boolean isRunning() {
        return isTimerRunning();
    }

    /**
     * Turn amount of seconds in MM:SS or H:MM:SS format
     *
     * @param seconds Passed seconds
     * @return String of Minutes:Seconds or Hours:Minutes:Seconds
     */
    public static String formatSeconds(long seconds) {
        if(seconds < 60*60) {
            return String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        }
        return String.format(Locale.getDefault(), "%d:%02d:%02d", seconds / (60*60), (seconds / 60) % 60, seconds % 60);
    }

    public void updateTimerText() {
        // make sure timer doesn't pass 9:59:59
        if (getSecondsPassed() > TIME_LIMIT)
            setSecondsPassed(0);
        if (textTimer != null)
            textTimer.setText(formatSeconds(getSecondsPassed()));
    }

    public void setSecondsPassed(int seconds) {
        long started = getTimerPauseTime();
        long paused = (1000 * (long) seconds);
        if (seconds == 0 && !isRunning()) //these two lines caused some issues if you restart and hide
            started = 0; //while still 00:00, might cause issues.

        setTimerValues(isRunning(), started, paused);
        updateTimerText();
    }

    private void animateStuff() {
//        TransitionManager.beginDelayedTransition((ViewGroup) viewTimer.getParent());
//        layoutParams.topMargin = isTimerVisible() ? topMarginMax : 0;
        interpolator.setReversed(!isTimerVisible());
        viewTimer.startAnimation(animation);
    }

    /**
     * Called from Toolbar
     * @param item Timer menu item. Text is updated whether timer is shown or not.
     */
    public void toggleTimerVisibility(MenuItem item) {
        setTimerVisible(!isTimerVisible());
        animateStuff();

        if(isTimerVisible()) {
            // show timer
            item.setTitle(R.string.hide_timer);
        } else {
            // hide timer
            item.setTitle(R.string.show_timer);
        }
    }

    private void updateButtonImage() {
        startStopButton.setImageResource(isRunning() ?
                        R.drawable.ic_baseline_pause_24px :
                        R.drawable.ic_baseline_play_arrow_24px
        );
    }

    public void toggleTimer() {
        boolean running = !isRunning();
        long started = getTimerStartTime();
        long paused = getTimerPauseTime();

        if(running) {
            // start timer
            started = System.currentTimeMillis() - paused;
            paused = 0;

            repeatingCall.run();

        } else {
            // pause timer
            handler.removeCallbacks(repeatingCall);
            paused = System.currentTimeMillis() - started;
            updateTimerText();
        }

        setTimerValues(running, started, paused);
        updateButtonImage();
    }

    public boolean isTimerVisible() {
        return GlobalOptions.isTimerVisible();
    }

    public void resetTimer() {
        setTimerValues(isRunning(), isRunning() ? System.currentTimeMillis() : 0, 0);
        updateTimerText();
    }

}
