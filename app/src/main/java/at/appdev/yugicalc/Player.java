package at.appdev.yugicalc;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import at.appdev.yugicalc.history.Points;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class Player {

    int points;
    // points are added / subtracted only 1 second after the last click action of user
    // meanwhile the "zwischenergebnis" is saved in tmpCalc
    int tmpCalc;

    // the shared "1-second-waiter" for both players
    private static final Object timerLock = new Object();
    private static Timer sharedTimer;
    // the fancy countdown machine
    ValueAnimator animator;

    WeakReference<TextView> pointsView;
    WeakReference<TextView> tmpView;

    // Needed for main UI thread (animation) and undo / redo enable stuff
    private WeakReference<AppCompatActivity> currentActivity;

    public Player(int points) {
        this.points = points;
        this.tmpCalc = 0;
        this.animator = new ValueAnimator();
        animator.setInterpolator(new DecelerateInterpolator(2));

        animator.setEvaluator((TypeEvaluator<Integer>) (fraction, startValue, endValue) ->
                Math.round(startValue + (endValue - startValue) * fraction));

        this.pointsView = new WeakReference<>(null);
        this.tmpView = new WeakReference<>(null);
        this.currentActivity = new WeakReference<>(null);
    }

    /**
     * Called when view changes to make sure we still point to the correct text fields
     * @param pointsView Player's current points
     * @param tmpView Temporary calculations (for those 1sec delays)
     * @param activity Needed for main UI thread (animation) and undo / redo enable stuff
     */
    void updateActivity(TextView pointsView, TextView tmpView, AppCompatActivity activity) {
        this.pointsView = new WeakReference<>(pointsView);
        this.tmpView = new WeakReference<>(tmpView);
        this.currentActivity = new WeakReference<>(activity);
        updatePointsText();
    }

    void releaseActivity() {
        cancelTimer();
        pointsView.clear();
        tmpView.clear();
        currentActivity.clear();
    }

    void updatePointsText() {
        TextView view = pointsView.get();
        if(view == null)
            return;

        view.setText(String.valueOf(points));
    }

    void setPointsText(String text) {
        TextView view = pointsView.get();
        if(view == null)
            return;

        view.setText(text);
    }

    void updateTmpText() {
        setTmpText(tmpCalc);
    }

    void setTmpText(int value) {
        TextView view = tmpView.get();
        if(view == null)
            return;

        if(value == 0) {
            view.setText("");
        } else {
            String content = (value > 0 ? "+" : "") + value + " ";
            view.setText(content);
        }
    }

    /**
     * Cancel 1 second delay.
     * If points animation is running, act as if that animation is already done.
     */
    public void cancelTimer() {
        cancelSharedTimer();
        tmpCalc = 0;

        if(animator.isRunning()) {
            animator.cancel();
            updatePointsText();
        }
        updateTmpText();
    }

    void reset() {
        reset(GlobalOptions.getStartingLifePoints());
    }

    void reset(int points) {
        cancelTimer();

        this.tmpCalc = 0;
        this.points = points;

        TextView view = tmpView.get();
        if(view != null) {
            view.setText("");
        }
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
        AppCompatActivity activity = currentActivity.get();
        if(activity == null) {
            return;
        }

        activity.runOnUiThread(() -> {
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
        commitPendingCalculations();
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
        updateTmpText();
        GameInformation.history.add(new Points(GameInformation.p1.points, GameInformation.p2.points));
        determineButtonEnable();
    }

    /**
     * Compute amount + temporary calculation.
     * If no delay, immediately add temporary calculation to player's points.
     * Otherwise wait a second. Timer is reset if during that time frame more points are added.
     * @param amount of points to add / subtract to temporary value
     * @param withDelay wait 1 second before adding / subtracting points to player.
     */
    public void calculate(int amount, boolean withDelay) {
        //if(amount == 0) //cancelWait() from MainActivity sends calculate with 0 to stop waitdelay
        //    return;

        cancelRunningAnimation();

        int wait = (withDelay ? 1000 : 0);
        synchronized(timerLock) {
            cancelSharedTimerLocked();
            tmpCalc += amount;
            sharedTimer = new Timer();
            sharedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    commitPendingCalculations();
                }
            }, wait);
        }
        updateTmpText();
    }

    private static void cancelSharedTimer() {
        synchronized(timerLock) {
            cancelSharedTimerLocked();
        }
    }

    private static void cancelSharedTimerLocked() {
        if(sharedTimer != null) {
            sharedTimer.cancel();
            sharedTimer = null;
        }
    }

    private void cancelRunningAnimation() {
        if(animator.isRunning()) {
            animator.cancel();
            updatePointsText();
            updateTmpText();
        }
    }

    private static void commitPendingCalculations() {
        Player p1 = GameInformation.p1;
        Player p2 = GameInformation.p2;
        int p1Tmp;
        int p2Tmp;
        int p1Start;
        int p2Start;

        synchronized(timerLock) {
            cancelSharedTimerLocked();
            p1Tmp = p1.tmpCalc;
            p2Tmp = p2.tmpCalc;

            if(p1Tmp == 0 && p2Tmp == 0) {
                return;
            }

            p1Start = p1.points;
            p2Start = p2.points;
            p1.points += p1Tmp;
            p2.points += p2Tmp;
            p1.tmpCalc = 0;
            p2.tmpCalc = 0;
        }

        if(p1Tmp != 0) {
            p1.animatePoints(p1Start, p1Tmp);
        }

        if(p2Tmp != 0) {
            p2.animatePoints(p2Start, p2Tmp);
        }

        GameInformation.history.add(new Points(p1.points, p2.points));
        determineButtonEnable();
    }

    private static void determineButtonEnable() {
        AppCompatActivity activity = GameInformation.p1.currentActivity.get();
        if(!(activity instanceof ButtonDeterminer)) {
            activity = GameInformation.p2.currentActivity.get();
        }

        if(activity instanceof ButtonDeterminer) {
            ((ButtonDeterminer) activity).determineButtonEnable();
        }
    }
}
