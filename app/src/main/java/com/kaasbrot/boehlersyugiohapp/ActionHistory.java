package com.kaasbrot.boehlersyugiohapp;

import java.util.ArrayList;
import java.util.List;

public class ActionHistory {

    public class Points {
        final int p1, p2;

        Points(int p1, int p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private int index;
    private List<Points> history;

    public ActionHistory(int startP1, int startP2) {
        history = new ArrayList<>();
        history.add(new Points(startP1, startP2));
        index = 0;
    }

    public void add(int p1, int p2) {
        // if points are the same as before, ignore
        Points current = history.get(index);
        if(p1 == current.p1 && p2 == current.p2)
            return;

        // if index is in the middle of the list (which happens after undo),
        // remove everything after index
        index++;
        if(index < history.size())
            history.subList(index, history.size()).clear();

        history.add(new Points(p1, p2));
    }

    public Points undo() {
        if(canUndo())
            index--;
        return history.get(index);
    }

    public Points redo() {
        if(canRedo())
            index++;
        return history.get(index);
    }

    public boolean canUndo() {
        return index > 0;
    }

    public boolean canRedo() {
        return index < history.size() - 1;
    }
}
