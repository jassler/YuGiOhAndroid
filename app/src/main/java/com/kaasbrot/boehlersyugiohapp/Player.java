package com.kaasbrot.boehlersyugiohapp;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.history.Points;

import java.util.Timer;
import java.util.TimerTask;

public class Player {

    int points;
    // points are added / subtracted only 1 second after the last click action of user
    // meanwhile the "zwischenergebnis" is saved in tmpCalc
    int tmpCalc;

    // the "1-second-waiter"
    Timer timer;
    // the fancy countdown machine
    ValueAnimator animator;

    TextView pointsView;
    TextView tmpView;

    // Needed for main UI thread (animation) and undo / redo enable stuff
    private AppCompatActivity currentActivity;

    public Player(int points) {
        this.points = points;
        this.tmpCalc = 0;
        this.timer = null;
        this.animator = new ValueAnimator();
        animator.setInterpolator(new DecelerateInterpolator(2));

        animator.setEvaluator((TypeEvaluator<Integer>) (fraction, startValue, endValue) ->
                Math.round(startValue + (endValue - startValue) * fraction));

        this.pointsView = null;
        this.tmpView = null;

        this.currentActivity = null;
    }

    /**
     * Called when view changes to make sure we still point to the correct text fields
     * @param pointsView Player's current points
     * @param tmpView Temporary calculations (for those 1sec delays)
     * @param activity Needed for main UI thread (animation) and undo / redo enable stuff
     */
    void updateActivity(TextView pointsView, TextView tmpView, AppCompatActivity activity) {
        this.pointsView = pointsView;
        this.tmpView = tmpView;
        this.currentActivity = activity;
        updatePointsText();
    }

    void updatePointsText() {
        pointsView.setText(String.valueOf(points));
    }

    void setPointsText(String text) {
        pointsView.setText(text);
    }

    void updateTmpText() {
        setTmpText(tmpCalc);
    }

    void setTmpText(int value) {
        if(value == 0) {
            tmpView.setText("");
        } else {
            String content = (value > 0 ? "+" : "") + value + " ";
            tmpView.setText(content);
        }
    }

    /**
     * Cancel 1 second delay.
     * If points animation is running, act as if that animation is already done.
     */
    void cancelTimer() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }

        if(animator.isRunning()) {
            animator.cancel();
            updatePointsText();
            updateTmpText();
        }
    }

    void reset() {
        reset(GlobalOptions.getStartingLifePoints());
    }

    void reset(int points) {
        cancelTimer();

        this.tmpCalc = 0;
        this.points = points;

        tmpView.setText("");
        updatePointsText();
    }

    /**
     * Count player's points down / up.
     * If the difference in points is big (eg. change from 7000 -> 8500 points),
     * it takes more time.
     * @param points Starting point (eg. 7000)
     * @param added Difference (eg. +1500)
     */
    private void animatePoints(final Integer points, final Integer added) {
        currentActivity.runOnUiThread(() -> {
            animator.setObjectValues(added, 0);

            // if tmpText should be updated as well, use pre
            animator.removeAllUpdateListeners();
            animator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                setPointsText(String.valueOf(points + added - value));
                setTmpText(value);
            });

            // animation duration, at least 0.5sec, at most 2sec
            int duration = Math.abs(added) / 2 + 250;
            if(duration < 500)
                duration = 500;
            else if(duration > 2000)
                duration = 2000;
            animator.setDuration(duration);
            animator.start();
        });
    }

    /**
     * Subtract half of player's points.
     * If points are uneven, points are rounded up.
     */
    public void divide() {
        cancelTimer();

        // subtract half of the player's points
        // since it's an integer, it's automatically rounded down.
        // eg. if player has 3 points -> subtract (int)(-3/2) = (int)(-1.5) = -1
        tmpCalc = -points / 2;
        if(tmpCalc == 0)
            return;

        animatePoints(points, tmpCalc);

        points += tmpCalc;
        tmpCalc = 0;
        GameInformation.history.add(new Points(GameInformation.p1.points, GameInformation.p2.points));
        if(currentActivity instanceof ButtonDeterminer)
            ((ButtonDeterminer) currentActivity).determineButtonEnable();
    }

    /**
     * Compute amount + temporary calculation.
     * If no delay, immediately add temporary calculation to player's points.
     * Otherwise wait a second. Timer is reset if during that time frame more points are added.
     * @param amount of points to add / subtract to temporary value
     * @param withDelay wait 1 second before adding / subtracting points to player.
     */
    public void calculate(int amount, boolean withDelay) {
        if(amount == 0)
            return;

        cancelTimer();

        tmpCalc += amount;
        updateTmpText();

        int wait = (withDelay ? 1000 : 0);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(tmpCalc == 0) {
                    return;
                }

                animatePoints(points, tmpCalc);

                points += tmpCalc;
                tmpCalc = 0;
                GameInformation.history.add(new Points(GameInformation.p1.points, GameInformation.p2.points));
                if(currentActivity instanceof ButtonDeterminer)
                    ((ButtonDeterminer) currentActivity).determineButtonEnable();
            }
        }, wait);
    }
}
