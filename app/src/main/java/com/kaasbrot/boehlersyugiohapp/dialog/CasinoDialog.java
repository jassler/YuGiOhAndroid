package com.kaasbrot.boehlersyugiohapp.dialog;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.Coin;
import com.kaasbrot.boehlersyugiohapp.history.Dice;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.lang.reflect.Array;
import java.util.Random;

public class CasinoDialog extends AppCompatDialogFragment {

    private final Random rand = new Random();

    private History history;

    //private Coin c;
    private Dice d;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        d = new Dice(rand.nextInt(6) + 1);
        builder.setMessage(R.string.dice_title); //centered title with bigger font than default title

        LayoutInflater factory = LayoutInflater.from(getContext());
        View view = factory.inflate(R.layout.dice_layout, null);

        //define images, for rare dice source can be changed here with if statement
        int[] imgPaths = {
                R.drawable.d1,
                R.drawable.d2,
                R.drawable.d3,
                R.drawable.d4,
                R.drawable.d5,
                R.drawable.d6
        };

        //defining variables that will be changed for animations
        ImageView imaged;
        imaged = view.findViewById(R.id.dicep);
        imaged.setImageResource(imgPaths[1]);   //dice starts at 1
        TextView textd1;                        //default size is 40, needs to get smaller
        TextView textd2;                        //default size is 10, needs to get bigger
        textd1 = view.findViewById(R.id.dicet1);
        textd2 = view.findViewById(R.id.dicet2);
        builder.setView(view);

        //2 delays at the moment, will need for loop that does this x times
        //textd1 and textd2 size should always add to the same number
        //should read size out of dice_layout, add them and use that number
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                imaged.setImageResource(imgPaths[4]);
                textd1.setTextSize(10);
                textd2.setTextSize(40);
                builder.setView(view);
            }
        },500);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                imaged.setImageResource(imgPaths[d.roll-1]); //final roll is correct
                textd1.setTextSize(40);
                textd2.setTextSize(10);
                builder.setView(view);
            }
        },1000);

        /*builder.setMessage(Html.fromHtml(d.asHtml(), Html.FROM_HTML_MODE_COMPACT))
                .setTitle(R.string.dice_title);*/
        if(history != null) {
            //history.add(c);
            history.add(d);
            }
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
    public Dice getDice() {
        return d;
    }

    public void setDice(Dice d) {
        this.d = d;
    }

    /* public Coin getCoin() {
        return c;
    }

    public void setCoin(Coin c) {
        this.c = c;
    }
        */
    public void setHistory(History history) {
        this.history = history;
    }
}
