package com.kaasbrot.boehlersyugiohapp.history;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Coins implements HistoryElement {
    public final Coin[] coins;

    public Coins() {
        Random rand = new Random();
        this.coins = new Coin[] {
                new Coin(rand.nextBoolean()),
                new Coin(rand.nextBoolean()),
                new Coin(rand.nextBoolean())
        };
    }

    public String asHtml() {
        return getCoinsHtml(coins);
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
        TextView text = view.findViewById(R.id.info);
        Resources res =  text.getResources();
        text.setText(getCoinsWords(
                coins,
                res.getString(R.string.result_heads),
                res.getString(R.string.result_tails)
        ));
        return view;
    }

    public static String getCoinsHtml(Coin[] coins) {
        String sequence = Arrays.stream(coins)
                .map(coin -> coin.toss.html)
                .collect(Collectors.joining("<br>"));
        return sequence;
    }

    public static String getCoinsWords(Coin[] coins, String headStr, String tailStr) {
        String sequence = Arrays.stream(coins)
                .map(coin -> coin.toss == Coin.Toss.HEADS ? headStr : tailStr)
                .collect(Collectors.joining(", "));
        return sequence;
    }
}
