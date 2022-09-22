package com.kaasbrot.boehlersyugiohapp;

import com.kaasbrot.boehlersyugiohapp.history.History;

public class GameInformation {

    static Player p1;
    static Player p2;

    static History history;

    static {
        p1 = new Player(8000);
        p2 = new Player(8000);
        history = new History(p1.points, p2.points);
    }
}
