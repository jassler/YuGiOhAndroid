package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.Gravity;
import android.widget.ImageView;
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
    //private Dice d;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        //new AlertDialog.Builder(getActivity());
        c = new Coin(rand.nextBoolean() ? Coin.Toss.HEADS : Coin.Toss.TAILS);
        

        builder.setMessage(Html.fromHtml(c.toss.html, Html.FROM_HTML_MODE_COMPACT))
                .setTitle("Münze");

        if(history != null) {
            history.add(c);
        //    history.add(d);
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
