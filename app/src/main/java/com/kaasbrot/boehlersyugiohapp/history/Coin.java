package com.kaasbrot.boehlersyugiohapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

public class Coin implements HistoryElement {

    public enum Toss {
        HEADS('\u2461', "Heads" ), //&#x24DA;
        TAILS('\u24D7', "Tails" ); //&#x24E9;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return toss == coin.toss;
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
        TextView text = view.findViewById(R.id.info);
        //text.setText(String.valueOf(toss.unicode));
        text.setText(R.string.coin_lands_on + (toss==Toss.HEADS ? R.string.result_heads : R.string.result_tails )+".");
        //text.setTextSize(32);

        return view;
    }
}
