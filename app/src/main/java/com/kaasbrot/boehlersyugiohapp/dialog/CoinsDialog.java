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
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.Coin;
import com.kaasbrot.boehlersyugiohapp.history.Coins;
import com.kaasbrot.boehlersyugiohapp.history.History;

import java.util.Random;

public class CoinsDialog extends AppCompatDialogFragment {

    private final Random rand = new Random();

    private History history;

    private Coins cs;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        //c = new Coin(rand.nextBoolean() ? Coin.Toss.HEADS : Coin.Toss.TAILS);
        cs = new Coins(rand.nextInt(7) + 1);

        builder.setMessage(Html.fromHtml(cs.asHtml(), Html.FROM_HTML_MODE_COMPACT))
                .setTitle("Drei Münzen");

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
        texts.setTextSize(144);
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
