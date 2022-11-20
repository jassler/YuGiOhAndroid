package com.kaasbrot.boehlersyugiohapp.history;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import kotlin.NotImplementedError;

public class HistoryElementParser {
    public ArrayList<LinkedTreeMap> actions;
    public boolean isNewGame;
    public int p1 = 0;
    public int p2 = 0;

    public Points parse() {
        Points p = new Points(p1, p2, isNewGame);

        if(actions == null)
            return p;

        for(LinkedTreeMap el : actions) {
            if(el.containsKey("roll")) {
                p.addAction(new Dice((int) ((double) el.get("roll"))));
            } else if(el.containsKey("toss")) {
                p.addAction(new Coin("HEADS".equals(el.get("toss"))));
            }
        }

        return p;
    }
}
