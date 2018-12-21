package com.kaasbrot.boehlersyugiohapp;

public class GameInformation {

    static Player p1;
    static Player p2;

    static ActionHistory history;

    static {
        p1 = new Player(8000);
        p2 = new Player(8000);
        history = new ActionHistory(p1.points, p2.points);
    }
}
