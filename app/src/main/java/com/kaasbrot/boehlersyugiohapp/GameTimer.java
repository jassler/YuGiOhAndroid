package com.kaasbrot.boehlersyugiohapp;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameTimer {

    private Handler handler;
    private long started;
    private long paused = 0;

    private boolean running;
    private boolean timerVisible;

    private ValueAnimator animator;

    private View viewTimer;
    private View activity_main;
    private TextView textTimer;
    private ImageButton startStopButton;

    private int marginTop = 0;

    private Runnable repeatingCall = new Runnable() {
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
        handler = new Handler();
        running = false;
        animator = new ValueAnimator();
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
    }

    public void updateActivity(View viewTimer, TextView textTimer, View startStopButton) {
        this.viewTimer = viewTimer;
        this.textTimer = textTimer;
        this.startStopButton = (ImageButton) startStopButton;

        ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams) viewTimer.getLayoutParams();
        layout.setMargins(layout.leftMargin, marginTop, layout.rightMargin, layout.bottomMargin);

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

    public String getSecondsPassedString() {
        return formatSeconds(getSecondsPassed());
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
            return String.format("%02d:%02d", seconds / 60, seconds % 60);
        }
        return String.format("%d:%02d:%02d", seconds / (60*60), (seconds / 60) % 60, seconds % 60);
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

    /**
     * Make timer slide up and down
     * @param from y Position element should start from
     * @param to y Position element should move to
     * @param layout Layout of element to be moved
     */
    public void animateTimerMovement(int from, int to, ConstraintLayout.LayoutParams layout) {
        if(animator.isRunning())
            animator.cancel();

        animator.setObjectValues(from, to);

        int left = layout.leftMargin, bottom = layout.bottomMargin, right = layout.rightMargin;

        animator.addUpdateListener(animation -> {
            String strValue = String.valueOf(animation.getAnimatedValue());
            int value = Integer.parseInt(strValue);
            layout.setMargins(left, value, right, bottom);
        });

        animator.setDuration(500);
        animator.start();
    }

    /**
     * Called from Toolbar
     * If timer is running, stop and hide timer.
     * If timer is not running, start and update timer display every second.
     * @param item Timer menu item. Text is updated whether timer is shown or not.
     */
    public void toggleTimerVisibility(MenuItem item) {
        timerVisible = !timerVisible;
        ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams) viewTimer.getLayoutParams();

        if(timerVisible) {
            // show timer
            item.setTitle(R.string.hide_timer);
            //marginTop = textTimer.getHeight() + 8; //old animation
            //animateTimerMovement(layout.topMargin, marginTop, layout);

            //if(running) { repeatingCall.run(); }
            toggleTimer();
            toggleTimer();
        } else {
            // hide timer
            item.setTitle(R.string.show_timer);
            //marginTop = 0; //old animation
            //animateTimerMovement(layout.topMargin, marginTop, layout);

            // don't have to update timer every second
            handler.removeCallbacks(repeatingCall);
        }
    }

    public int getCurrentMarginTop() {
        return marginTop;
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

    public void resetTimer() {
        started = System.currentTimeMillis();
        paused = 0;
        updateTimerText();
    }

    public boolean isTimerVisible() {
        return timerVisible;
    }

    public interface GameTimerToggleListener {
        /**
         * Call toggling before toggling timer.
         *
         * @param willBeRunning Is timer about to start or not
         * @return false if toggling should be prevented, true if everything should continue as expected
         */
        boolean toggling(boolean willBeRunning);
    }

}
