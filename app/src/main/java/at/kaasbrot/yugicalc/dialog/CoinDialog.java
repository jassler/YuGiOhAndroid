package at.kaasbrot.yugicalc.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import at.kaasbrot.yugicalc.R;
import at.kaasbrot.yugicalc.history.Coin;
import at.kaasbrot.yugicalc.history.History;

import java.util.Random;

public class CoinDialog extends AppCompatDialogFragment {

    private final Random rand = new Random();

    private History history;

    private Coin c;
    //used in playAnimation
    private int timerotation;
    private int numrotation;
    private TextView titleView;
    //used in playOtherAnimation
    private AnimatorSet animation;
    private Animator animator;

    private Animation coinAnimation;

    private void playOtherAnimation(ImageView imaged) {
        if(coinAnimation != null && !coinAnimation.hasEnded()) {
            return;
        }

        imaged.setImageResource(R.drawable.heads_aa);
        coinAnimation = new CoinAnimation(imaged, R.drawable.heads_aa, R.drawable.tails_aa, 0, 180, 0,0, 0, 0);
        coinAnimation.setRepeatCount(5);
        coinAnimation.setDuration(350);
        coinAnimation.setInterpolator(new LinearInterpolator());
        imaged.startAnimation(coinAnimation);
    }


    String last;
    private void playAnimation(ImageView imaged) {
        if(animator != null && animator.isRunning()) {
            return;
        }
        if(c == null){last="h";}else if(c.toss == Coin.Toss.HEADS) {
            last="h";
        } else {
            last="t";
        };
        c = new Coin(rand.nextBoolean());
        timerotation = 150*2; //should be even
        numrotation = 4;
        if(history != null)
            history.add(c);

        Double[] timingsrotation = { //timings for 6 rotations
                0.92,
                1.7,
                2.34,
                3.1
        };
        /*Double[] timingsrotation = {
                1.1,
                2.0,
                2.78,
                3.39,
                4.0,
                4.96
        };*/ //timings for 6 rotations
        /*Double[] timingsrotation = {
                1.3,
                2.3,
                3.1,
                3.70,
                4.3,
                4.95,
                5.75,
                6.65
        };*/ //timings for 8 rotations
        /*Double[] timingsrotation = {
                1.43,
                2.48,
                3.18,
                3.88,
                4.47,
                5.19,
                5.81,
                6.59,
                7.56
        };*/ //timings for 9 rotations

        // need to set transparent first, otherwise circle with transparent background
        // is just printed on top of other image
        imaged.setImageResource(android.R.color.transparent);
        //imaged.setImageResource(R.drawable.heails);
        if(last.equals("h")) {
            imaged.setImageResource(R.drawable.heads_aa);
        } else {
            imaged.setImageResource(R.drawable.tails_aa);
        }

        this.animator = ObjectAnimator.ofFloat(imaged, View.ROTATION_Y, 0, 180*numrotation);
        this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.animator.setDuration(timerotation*numrotation);
        this.animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animator) {
                if(getDialog()!= null){
                    TextView title = getDialog().findViewById(android.R.id.message);
                    title.setText("..");

                }
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        if(getDialog() != null) {
                            TextView title = getDialog().findViewById(android.R.id.message);
                            title.setText("...");
                        }
                    }
                },(long)(timerotation*numrotation/2));
            }
            public void onAnimationEnd(Animator animator) {
                if(getDialog() == null) {
                    return;
                }
                TextView title = getDialog().findViewById(android.R.id.message);
                if(c.toss == Coin.Toss.HEADS) {
                    //imaged.setImageResource(R.drawable.heads_aa);
                    title.setText(R.string.result_Heads);
                } else {
                    //imaged.setImageResource(R.drawable.tails_aa);
                    title.setText(R.string.result_Tails);
                }
            }
            public void onAnimationCancel(Animator animator) {}
            public void onAnimationRepeat(Animator animator) {}
        });

        for (int i = 1; i < numrotation; i++) {
            if((i %2)==1){
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imaged.setImageResource(R.drawable.heailsm);
                }
            }, (long)(timerotation* timingsrotation[i-1]));
            }else{
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imaged.setImageResource(R.drawable.heails);
                }
            }, (long)(timerotation* timingsrotation[i-1]));
            }
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(c.toss == Coin.Toss.HEADS) {
                    imaged.setImageResource(R.drawable.heads_aa);
                } else {
                    imaged.setImageResource(R.drawable.tails_aa);
                }
            }
        },(long)(timerotation*timingsrotation[timingsrotation.length-1]));

        this.animator.start();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

        LayoutInflater factory = LayoutInflater.from(getContext());
        View view = factory.inflate(R.layout.coin, null);
        builder.setView(view);
        builder.setMessage("..");

        // make sure coin is not cut off at the top and bottom
        view.setClipToOutline(false);

        ImageView imaged = view.findViewById(R.id.coinImage);
        imaged.setScaleX(1f);
        imaged.setScaleY(1f);

        imaged.setOnClickListener(view1 -> playAnimation(imaged));
        playAnimation(imaged);

        builder.setPositiveButton("ok", (dialogInterface, i) -> {});
        return builder.create();

    }

    @Override
    public void onStart() {
        // once the dialog is built and shown, we can adjust text size and font alignment
        super.onStart();
        TextView texts = this.getDialog().findViewById(android.R.id.message);
        texts.setTextSize(30); //144
        texts.setGravity(Gravity.CENTER);
        this.titleView = texts;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.titleView = null;
        this.animator.cancel();
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
