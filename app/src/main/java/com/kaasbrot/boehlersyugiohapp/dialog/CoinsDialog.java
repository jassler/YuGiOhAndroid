package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.os.Bundle;
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
import com.kaasbrot.boehlersyugiohapp.history.Coins;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

public class CoinsDialog extends AppCompatDialogFragment {

    public final Random rand = new Random();

    private History history;

    public Coins cs;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        //c = new Coin(rand.nextBoolean() ? Coin.Toss.HEADS : Coin.Toss.TAILS);
        cs = new Coins();

        builder.setMessage(R.string.three_coins);

        LayoutInflater factory = LayoutInflater.from(getContext());

        View view = factory.inflate(R.layout.threecoins_layout, null);
        ImageView[] imgViews = new ImageView[] {
                view.findViewById(R.id.tci1),
                view.findViewById(R.id.tci2),
                view.findViewById(R.id.tci3)
        };

        for (int i = 0; i < imgViews.length; i++) {
            if(cs.coins[i].toss == Coin.Toss.HEADS) {
                imgViews[i].setImageResource(R.drawable.heads_aa);
            } else {
                imgViews[i].setImageResource(R.drawable.tails_aa);
            }
        }

        builder.setView(view);


        if(history != null) {
            //history.add(c);
            history.add(cs);
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
    public Coins getCoins() {
        return cs;
    }

    public void setCoins(Coins cs) {
        this.cs = cs;
    }

    public void setHistory(History history) {
        this.history = history;
    }
}
