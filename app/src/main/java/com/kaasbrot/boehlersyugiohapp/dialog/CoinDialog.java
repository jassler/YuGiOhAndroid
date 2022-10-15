package com.kaasbrot.boehlersyugiohapp.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.Coin;
import com.kaasbrot.boehlersyugiohapp.history.Dice;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.util.Random;

public class CoinDialog extends AppCompatDialogFragment {

    private final Random rand = new Random();

    private History history;

    private Coin c;
    private TextView titleView;
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

    private void play3dAnimation(ImageView imaged) {
        if(animation != null && animation.isRunning()) {
            return;
        }

        imaged.setImageResource(android.R.color.transparent);
        imaged.setBackgroundResource(R.drawable.coinframes);
        Drawable rocketAnimation = imaged.getBackground();
        if (rocketAnimation instanceof Animatable) {
            ((Animatable)rocketAnimation).start();
        }

        this.animation = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.anim.grow);
        this.animation.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animator) {}
            public void onAnimationEnd(Animator animator) {
                if (rocketAnimation instanceof Animatable) {
                    ((Animatable)rocketAnimation).stop();
                }
                imaged.setBackgroundResource(android.R.color.transparent);

                c = new Coin(rand.nextBoolean());
                if(c.toss == Coin.Toss.HEADS) {
                    if(titleView != null) titleView.setText(R.string.result_Heads);
                     imaged.setImageResource(R.drawable.heads_aa);
                } else {
                    if(titleView != null) titleView.setText(R.string.result_Tails);
                     imaged.setImageResource(R.drawable.tails_aa);
                }
                if(history != null)
                    history.add(c);
            }
            public void onAnimationCancel(Animator animator) {}
            public void onAnimationRepeat(Animator animator) {}
        });
        animation.setTarget(imaged);
        animation.start();
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
        // need to set transparent first, otherwise circle with transparent background
        // is just printed on top of other image
        imaged.setImageResource(android.R.color.transparent);
        //imaged.setImageResource(R.drawable.heails);
        if(last.equals("h")) {
            imaged.setImageResource(R.drawable.heads_aa);
        } else {
            imaged.setImageResource(R.drawable.tails_aa);
        }

        this.animator = ObjectAnimator.ofFloat(imaged, View.ROTATION_Y, 0, 8*180);
        this.animator = ObjectAnimator.ofFloat(imaged, View.ROTATION_Y, 0, 6*180);
        this.animator.setInterpolator(new AccelerateDecelerateInterpolator());
        this.animator.setDuration(1080);
        this.animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animator) {
                if(getDialog()!= null){
                    TextView title = getDialog().findViewById(android.R.id.message);
                    title.setText("...");
                }
            }
            public void onAnimationEnd(Animator animator) {
                TextView title = getDialog().findViewById(android.R.id.message);
                if(c.toss == Coin.Toss.HEADS) {
                    imaged.setImageResource(R.drawable.heads_aa);
                    title.setText(R.string.result_Heads);
                } else {
                    imaged.setImageResource(R.drawable.tails_aa);
                    title.setText(R.string.result_Tails);
                }
                if(history != null)
                    history.add(c);
            }
            public void onAnimationCancel(Animator animator) {}
            public void onAnimationRepeat(Animator animator) {}
        });

        this.animator.start();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                imaged.setImageResource(R.drawable.heails);
            }
        },125);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(c.toss == Coin.Toss.HEADS) {
                    imaged.setImageResource(R.drawable.heads_aa);
                } else {
                    imaged.setImageResource(R.drawable.tails_aa);
                }
            }
        },875);



    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

        LayoutInflater factory = LayoutInflater.from(getContext());
        View view = factory.inflate(R.layout.coin, null);
        builder.setView(view);
        builder.setMessage("...");

        // make sure coin is not cut off at the top and bottom
        view.setClipToOutline(false);

        ImageView imaged = view.findViewById(R.id.coinImage);
        imaged.setScaleX(0.5f);
        imaged.setScaleY(0.5f);

        imaged.setOnClickListener(view1 -> playOtherAnimation(imaged));
        playOtherAnimation(imaged);

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
    }

    public Coin getCoin() {
        return c;
    }

    public void setCoin(Coin c) {
        this.c = c;
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
