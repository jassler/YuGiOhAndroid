package com.kaasbrot.boehlersyugiohapp.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.GlobalOptions;
import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.Dice;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.util.Arrays;
import java.util.Random;

public class CasinoDialog extends AppCompatDialogFragment {

    private final Random rand = new Random();

    private History history;
    private Dice d;
    private final int imgtime=120; //100
    private final int animationtime=350; //300

    AnimatorSet animatorSet;

    int numfaces = (int)(Math.ceil((float)animationtime/imgtime));
    int[] dicesequence = {
            2,
            3,
            0,
            1,
            4,
            5
    };
    int[] imgPaths;

    private void updateImage(ImageView img, int i) {
        if(!animatorSet.isRunning()) return;
        img.setImageResource(imgPaths[dicesequence[i]]);
        new Handler().postDelayed(() -> updateImage(img, (i+1)%6), imgtime);
    }

    private void playAnimation(View view) {
        if(animatorSet != null && animatorSet.isRunning()) {
            return;
        }
        d = new Dice(rand.nextInt(6) + 1);
        //wo d in dicesequence
        int sequencegoal = Arrays.asList(dicesequence).indexOf(d.roll-1);
        int sequencestart = (sequencegoal-numfaces+600)%6;

        Animator upup = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -120f).setDuration(animationtime);
        Animator down = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, -120f, 0f).setDuration(animationtime);

        upup.setInterpolator(new DecelerateInterpolator());
        down.setInterpolator(new AccelerateInterpolator());

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(upup, down);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                        if(history != null) history.add(d);
                        ImageView endpic = view.findViewById(R.id.dicep);
                        endpic.setImageResource(imgPaths[d.roll-1]);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        animatorSet.start();
        updateImage(view.findViewById(R.id.dicep),sequencestart);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        builder.setMessage(R.string.dice_title); //centered title with bigger font than default title

        if(GlobalOptions.showSheep()) {
            imgPaths = new int[] {
                    R.drawable.ds1,
                    R.drawable.ds2,
                    R.drawable.ds3,
                    R.drawable.ds4,
                    R.drawable.ds5,
                    R.drawable.ds6
            };
        } else {
            imgPaths = new int[] {
                    R.drawable.d1,
                    R.drawable.d2,
                    R.drawable.d3,
                    R.drawable.d4,
                    R.drawable.d5,
                    R.drawable.d6
            };
        }

        LayoutInflater factory = LayoutInflater.from(getContext());
        View view = factory.inflate(R.layout.dice_layout, null);
        builder.setView(view);

        ImageView imaged = view.findViewById(R.id.dicep);
        imaged.setImageResource(imgPaths[1]);
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
