package com.kaasbrot.boehlersyugiohapp.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
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
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.Coin;
import com.kaasbrot.boehlersyugiohapp.history.Dice;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class CasinoDialog extends AppCompatDialogFragment {

    private History history;
    private final ImgPathsIterator imgPathsIterator;

    AnimatorSet animatorSet;

    public CasinoDialog() {
        this.imgPathsIterator = new ImgPathsIterator(
                true,
                R.drawable.d1,
                R.drawable.d2,
                R.drawable.d3,
                R.drawable.d4,
                R.drawable.d5,
                R.drawable.d6
        );
    }

    private void updateImage(ImageView img) {
        if(!animatorSet.isRunning()) return;
        img.setImageResource(imgPathsIterator.next());
        new Handler().postDelayed(() -> updateImage(img), 100);
    }

    private void playAnimation(View view) {
        if(animatorSet != null && animatorSet.isRunning()) {
            return;
        }

        Animator upup = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -100f).setDuration(300);
        Animator down = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -100f, 0f).setDuration(300);

        upup.setInterpolator(new DecelerateInterpolator());
        down.setInterpolator(new AccelerateInterpolator());

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(upup, down);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if(history != null) history.add(imgPathsIterator.generateDiceValue());
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animatorSet.start();
        updateImage(view.findViewById(R.id.dicep));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setMessage(R.string.dice_title); //centered title with bigger font than default title

        LayoutInflater factory = LayoutInflater.from(getContext());
        View view = factory.inflate(R.layout.dice_layout, null);
        builder.setView(view);

        ImageView imaged = view.findViewById(R.id.dicep);
        imaged.setOnClickListener(view1 -> playAnimation(view));
        playAnimation(view);
        builder.setPositiveButton("ok", (dialogInterface, i) -> {});
        return builder.create();
    }

    @Override
    public void onStart() {
        // once the dialog is built and shown, we can adjust text size and font alignment
        super.onStart();
        TextView texts = this.getDialog().findViewById(android.R.id.message);
        texts.setTextSize(30);
        texts.setGravity(Gravity.CENTER);
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
