package com.kaasbrot.boehlersyugiohapp.history;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History {

    // if last entry was less than <cooldown> ms ago, save as one action
    private static final long COOLDOWN = 200;

    private long lastEntry;
    private int lastEntryPlayer;

    private int index;
    private final List<HistoryElement> history;
    private SharedPreferences.Editor editor = null;

    public History(int startP1, int startP2) {
        this(new ArrayList<>(Collections.singletonList(new Points(startP1, startP2))));
    }

    public History(List<HistoryElement> history) {
        this.history = history;
        index = 0;
        lastEntry = 0;
        lastEntryPlayer = 0;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
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

        if(editor != null) {
            editor.putString("history", new Gson().toJson(this.history));
            // apply called asynchronously, calling commit would be done synchronously.
            // From my understanding, a second commit will wait for the first apply to finish.
            editor.apply();
        }
    }

    /**
     * Find last points object in History. If there is a NewGame element in the way,
     * it returns null to signalize: "Yo, this is the first points element of the game."
     *
     * @return Last points object in history, or null if it's the first points object after a
     * NewGame.
     */
    private Points prevPointsForDiff() {
        for (int i = index; i >= 0; i--) {
            HistoryElement element = history.get(i);
            if(element instanceof Points)
                return (Points) element;
            if(element instanceof NewGame)
                return null;
        }
        return null;
    }

    /**
     * Add new points object to game.
     *
     * @param p1 Current life points of player 1
     * @param p2 Current life points of player 2
     */
    public void add(int p1, int p2) {
        Points current = getLastPoints();

        // if points are the same as before, ignore
        if(p1 == current.p1 && p2 == current.p2)
            return;

        // if last entry less than <cooldown> ms ago, count as one action
        // also make sure, it's not the same player the points are subtracted from
        if (System.currentTimeMillis() - lastEntry <= COOLDOWN && (
                (p1 != current.p1 && p2 == current.p2 && lastEntryPlayer == 2) ||
                (p1 == current.p1 && p2 != current.p2 && lastEntryPlayer == 1)
        )) {
            history.set(index, new Points(p1, p2, prevPointsForDiff()));
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

            addToIndex(new Points(p1, p2, prevPointsForDiff()));
        }
    }

    /**
     * Add arbitrary HistoryElement object.
     *
     * NOTE: for Point objects, it is strongly advised
     * to call {@link #add(int p1, int p2)} instead.
     *
     * @param e History element
     */
    public void add(HistoryElement e) {
        addToIndex(e);
    }

    /**
     * Get the most current Point object in history.
     *
     * History may be muddied with NewGame, Coin, Dice
     * or other HistoryElement objects, so to easily access the current life points for each
     * player, this method may be called.
     *
     * @return Most current Point object
     */
    public Points getLastPoints() {
        int i = index;
        while (!(history.get(i) instanceof Points)) {
            i--;
        }

        return (Points) history.get(i);
    }

    /**
     * Deletes everything in history, only keeps the most current points (see {@link #getLastPoints()}.
     */
    public void clearHistory() {
        Points p = getLastPoints();
        history.clear();
        history.add(p);
        index = 0;
        lastEntryPlayer = 0;
        lastEntry = 0;
    }

    public Points undo() {
        if (!canUndo()) {
            return getLastPoints();
        }

        lastEntryPlayer = 0;
        do {
            index--;
        } while (canUndo() && !(history.get(index) instanceof Points));

        return (Points) history.get(index);
    }

    public Points redo() {
        if (!canRedo()) {
            return getLastPoints();
        }

        lastEntryPlayer = 0;
        do {
            index++;
        } while (canRedo() && !(history.get(index) instanceof Points));

        return getLastPoints();
    }

    public boolean canUndo() {
        return index > 0;
    }

    public boolean canRedo() {
        return index < history.indexOf(getLastPoints());
    }

    public HistoryElement getCurrentEntry() {
        return history.get(index);
    }

    public List<HistoryElement> getHistory() {
        return history;
    }
}
