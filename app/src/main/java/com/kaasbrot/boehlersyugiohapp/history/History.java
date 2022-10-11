package com.kaasbrot.boehlersyugiohapp.history;

import java.util.ArrayList;
import java.util.List;

public class History {

    // if last entry was less than <cooldown> ms ago, save as one action
    private static final long COOLDOWN = 200;

    private long lastEntry;
    private int lastEntryPlayer;

    private int index;
    private final List<HistoryElement> history;

    public History(int startP1, int startP2) {
        history = new ArrayList<>();
        history.add(new Points(startP1, startP2, this::prevPointsFrom));
        index = 0;
        lastEntry = 0;
        lastEntryPlayer = 0;
    }

    public Points prevPointsFrom(Points p) {
        for (int i = history.indexOf(p) - 1; i >= 0; i--) {
            HistoryElement element = history.get(i);
            if(element instanceof Points)
                return (Points) element;
            if(element instanceof NewGame)
                return null;
        }
        return null;
    }

    private void addToIndex(HistoryElement element) {
        // if index is in the middle of the list (which happens after undo),
        // remove everything after index
        index++;
        if (index < history.size()) {
            history.subList(index, history.size()).clear();
        }
        history.add(element);
        index = history.size() - 1;
    }

    public void add(int p1, int p2) {
        Points current = lastPoints();

        // if points are the same as before, ignore
        if(p1 == current.p1 && p2 == current.p2)
            return;

        // if last entry less than <cooldown> ms ago, count as one action
        // also make sure, it's not the same player the points are subtracted from
        if (System.currentTimeMillis() - lastEntry <= COOLDOWN && (
                (p1 != current.p1 && p2 == current.p2 && lastEntryPlayer == 2) ||
                (p1 == current.p1 && p2 != current.p2 && lastEntryPlayer == 1)
        )) {
            history.set(index, new Points(p1, p2, this::prevPointsFrom));
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

            addToIndex(new Points(p1, p2, this::prevPointsFrom));
        }
    }

    public void add(HistoryElement e) {
        addToIndex(e);
    }

    public Points lastPoints() {
        int i = index;
        while (!(history.get(i) instanceof Points)) {
            i--;
        }

        return (Points) history.get(i);
    }

    public void clearHistory() {
        int lastPoints = history.indexOf(lastPoints());
        if(history.size() > 1 && lastPoints > 0) {
            history.subList(0, lastPoints).clear();
            index = history.size() - 1;
            lastEntryPlayer = 0;
            lastEntry = 0;
        }
    }

    public Points undo() {
        if (!canUndo()) {
            return lastPoints();
        }

        lastEntryPlayer = 0;
        do {
            index--;
        } while (canUndo() && !(history.get(index) instanceof Points));

        return (Points) history.get(index);
    }

    public Points redo() {
        if (!canRedo()) {
            return lastPoints();
        }

        lastEntryPlayer = 0;
        do {
            index++;
        } while (canRedo() && !(history.get(index) instanceof Points));

        return lastPoints();
    }

    public boolean canUndo() {
        return index > 0;
    }

    public boolean canRedo() {
        return index < history.size() - 1;
    }

    public HistoryElement getCurrentEntry() {
        return history.get(index);
    }

    public List<HistoryElement> getHistory() {
        return history;
    }
}
