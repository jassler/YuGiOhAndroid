package com.kaasbrot.boehlersyugiohapp;

import android.animation.ValueAnimator;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class GameTimer {

    private final Handler handler;
    private long started;
    private long paused;

    private boolean running;
    private boolean timerVisible;

    private final ReverseInterpolator interpolator;
    private final Animation animation;
    private int topMarginMax = 650;

    private View viewTimer;
    private TextView textTimer;
    private ImageButton startStopButton;
    private ConstraintLayout.LayoutParams layoutParams;

    private final Runnable repeatingCall = new Runnable() {
        @Override
        public void run() {
            // make sure timer doesn't pass 9:59:59
            if (getSecondsPassed() > 9 * 60 * 60 + 59 * 60 + 59)
                return;
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

        this.running = GlobalOptions.isTimerRunning();
        this.started = GlobalOptions.getTimerStartTime();
        this.paused = GlobalOptions.getTimerPauseTime();

        this.animation.setDuration(400);
        this.animation.setInterpolator(interpolator);
    }

    public void setTopMarginMax(int max) {
        this.topMarginMax = max;
        if(timerVisible) {
            this.layoutParams.topMargin = max;
            this.viewTimer.requestLayout();
        }
    }

    public void updateActivity(View viewTimer) {
        this.viewTimer = viewTimer;
        this.textTimer = viewTimer.findViewById(R.id.timerText);
        this.startStopButton = viewTimer.findViewById(R.id.timerPlayPauseButton);
        this.layoutParams = (ConstraintLayout.LayoutParams) viewTimer.getLayoutParams();

        layoutParams.topMargin = timerVisible ? topMarginMax : 0;
        viewTimer.requestLayout();

        if(!running && started == 0) {
            textTimer.setText("00:00");
        } else {
            updateTimerText();
        }
        updateButtonImage();
    }

    public long getSecondsPassed() {
        if (paused > 0.001) {
            return paused / 1000;
        }
        if (started == 0) {
            return 0;
        }
        return (System.currentTimeMillis() - started) / 1000;
    }

    public boolean isRunning() {
        return running;
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
        textTimer.setText(formatSeconds(getSecondsPassed()));
    }

    public void setSecondsPassed(int seconds) {
        paused = (1000 * (long) seconds);
        if (seconds == 0 && !isRunning()) //these two lines caused some issues if you restart and hide
            this.started = 0; //while still 00:00, might cause issues.
        updateTimerText();
    }

    private void animateStuff() {
        interpolator.setReversed(!timerVisible);
        viewTimer.startAnimation(animation);
    }

    /**
     * Called from Toolbar
     * @param item Timer menu item. Text is updated whether timer is shown or not.
     */
    public void toggleTimerVisibility(MenuItem item) {
        timerVisible = !timerVisible;
        animateStuff();

        if(timerVisible) {
            // show timer
            item.setTitle(R.string.hide_timer);

            //toggleTimer();
            //toggleTimer();
        } else {
            // hide timer
            item.setTitle(R.string.show_timer);

            // don't have to update timer every second
            //handler.removeCallbacks(repeatingCall);
        }
    }

    private void updateButtonImage() {
        if(running) {
            startStopButton.setImageResource(R.drawable.ic_baseline_pause_24px);
        } else {
            startStopButton.setImageResource(R.drawable.ic_baseline_play_arrow_24px);
        }
    }

    public void toggleTimer() {
        running = !running;

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

        updateButtonImage();
    }

    public boolean isTimerVisible() {
        return timerVisible;
    }

    public void resetTimer() {
        started = System.currentTimeMillis();
        paused = 0;
        updateTimerText();
    }

}
