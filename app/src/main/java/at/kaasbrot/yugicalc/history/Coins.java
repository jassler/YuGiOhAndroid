package at.kaasbrot.yugicalc.history;

import android.content.res.Resources;


import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import at.kaasbrot.yugicalc.R;

public class Coins implements HistoryAction {
    public final Coin[] coins;

    public Coins() {
        Random rand = new Random();
        this.coins = new Coin[] {
                new Coin(rand.nextBoolean()),
                new Coin(rand.nextBoolean()),
                new Coin(rand.nextBoolean())
        };
    }

    public Coins(Coin[] coins) {
        this.coins = coins;
    }

    public String asHtml() {
        return getCoinsHtml(coins);
    }

    @Override
    public String render(Resources res) {
        return getCoinsWords(
                coins,
                res.getString(R.string.result_heads),
                res.getString(R.string.result_tails)
        );
    }

    public static String getCoinsHtml(Coin[] coins) {
        return Arrays.stream(coins)
                .map(coin -> coin.toss.html)
                .collect(Collectors.joining("<br>"));
    }

    public static String getCoinsWords(Coin[] coins, String headStr, String tailStr) {
        return Arrays.stream(coins)
                .map(coin -> coin.toss == Coin.Toss.HEADS ? headStr : tailStr)
                .collect(Collectors.joining(", "));
    }
}
