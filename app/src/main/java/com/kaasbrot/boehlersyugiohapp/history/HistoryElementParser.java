package com.kaasbrot.boehlersyugiohapp.history;

import kotlin.NotImplementedError;

public class HistoryElementParser {
    public static Points prev = new Points(8000, 8000);
    public Coin.Toss toss;
    public Coin[] coins;

    public int roll = -1;

    public int p1 = 0;
    public int p2 = 0;
    public int diff1 = 0;
    public int diff2 = 0;

    public Points parse() {
//        if(toss != null)
//            return new Coin(toss);
//        if(coins != null)
//            return new Coins(coins);
//        if(roll != -1)
//            return new Dice(roll);
//        if(p1 == 0 && p2 == 0 && diff1 == 0 && diff2 == 0)
//            return new NewGame();
//        Points p = new Points(p1, p2, prev);
//        prev = p;
//        return p;
        throw new NotImplementedError();
    }
}
