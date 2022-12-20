package at.appdev.yugicalc;

import at.appdev.yugicalc.history.History;
import at.appdev.yugicalc.history.Points;

public class GameInformation {

    static Player p1;
    static Player p2;

    static History history;

    static {
        p1 = new Player(8000);
        p2 = new Player(8000);
        history = new History(new Points(p1.points, p2.points));
    }
}
