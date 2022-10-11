package com.kaasbrot.boehlersyugiohapp.history;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

import java.util.Random;

public class Coin implements HistoryElement {
    public enum Toss {
        HEADS('\u2461', "&#x24DA;" ),
        TAILS('\u24D7', "&#x24E9;" );

        public final char unicode;
        public final String html;
        Toss(char c, String html) {
            this.unicode = c;
            this.html = html;
        }
    }

    public final Toss toss;

    public Coin(Toss toss) {
        this.toss = toss;
    }

    public Coin(boolean isHeads) {
        this(isHeads ? Toss.HEADS : Toss.TAILS);
    }

    public int tossDrawable() {
        switch(toss) {
            case HEADS: return R.drawable.heads_aa;
            case TAILS: return R.drawable.tails_aa;
            default: throw new RuntimeException("Unknown Toss value " + toss);
        }
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
        TextView text = view.findViewById(R.id.info);

        Resources res = text.getResources();
        int pre = R.string.coin_lands_on;
        int post = (toss == Toss.HEADS) ? R.string.result_heads : R.string.result_tails;
        text.setText(String.format("%s %s.", res.getString(pre), res.getString(post)));
        return view;
    }
}
