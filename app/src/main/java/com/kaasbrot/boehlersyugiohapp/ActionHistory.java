package com.kaasbrot.boehlersyugiohapp;

import java.util.ArrayList;
import java.util.List;

public class ActionHistory {

    // if last entry was less than <cooldown> ms ago, save as one action
    private long lastEntry;
    private static final long COOLDOWN = 200;
    private int lastEntryPlayer;

    private int index;
    private List<Points> history;

    public static class Points {
        final int p1, p2;

        Points(int p1, int p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Points) {
                Points p = (Points) obj;
                return p.p1 == this.p1 && p.p2 == this.p2;
            }
            return obj.equals(this);
        }
    }

    public ActionHistory(int startP1, int startP2) {
        history = new ArrayList<>();
        history.add(new Points(startP1, startP2));
        index = 0;
        lastEntry = 0;
        lastEntryPlayer = 0;
    }

    public void add(int p1, int p2) {
        // if points are the same as before, ignore
        Points current = history.get(index);
        if(p1 == current.p1 && p2 == current.p2)
            return;

        // if last entry less than <cooldown> ms ago, count as one action
        // also make sure, it's not the same player the points are subtracted from
        if (
                System.currentTimeMillis() - lastEntry <= COOLDOWN &&
                        ((p1 != current.p1 && p2 == current.p2 && lastEntryPlayer == 2) ||
                                (p1 == current.p1 && p2 != current.p2 && lastEntryPlayer == 1))
        ) {
            history.set(index, new Points(p1, p2));
            lastEntryPlayer = 0;

        } else {
            // save time of last entry for cooldown
            lastEntry = System.currentTimeMillis();
            if (p1 != current.p1 && p2 == current.p2)
                lastEntryPlayer = 1;
            else if (p1 == current.p1 && p2 != current.p2)
                lastEntryPlayer = 2;
            else
                lastEntryPlayer = 0;

            // if index is in the middle of the list (which happens after undo),
            // remove everything after index
            index++;
            if (index < history.size())
                history.subList(index, history.size()).clear();

            history.add(new Points(p1, p2));
        }

    }

    public Points undo() {
        if (canUndo()) {
            index--;
            lastEntryPlayer = 0;
        }
        return history.get(index);
    }

    public Points redo() {
        if (canRedo()) {
            index++;
            lastEntryPlayer = 0;
        }
        return history.get(index);
    }

    public boolean canUndo() {
        return index > 0;
    }

    public boolean canRedo() {
        return index < history.size() - 1;
    }

    public Points getCurrentEntry() {
        return history.get(index);
    }

    public List<Points> getHistory() {
        return history;
    }
}
