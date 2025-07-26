package at.appdev.yugicalc.history;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import at.appdev.yugicalc.GlobalOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class History implements Iterable<Points> {

    // if last entry was less than <cooldown> ms ago, save as one action
    private static final long COOLDOWN = 200;

    private long lastEntry;
    private int lastEntryPlayer;

    private int index;
    private final List<Points> history;
    private SharedPreferences.Editor editor = null;

    public History(Points p) {
        this(new ArrayList<>(Collections.singletonList(p)));
    }

    public History(List<Points> history) {
        this.history = history;
        index = history.size() - 1;
        lastEntry = 0;
        lastEntryPlayer = 0;

        if(history.isEmpty()) {
            history.add(new Points(GlobalOptions.getStartingLifePoints()));
            index = 0;
        }
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    private void updateLocalStorage() {
        if(editor != null) {
            editor.putString(GlobalOptions.HISTORY, new Gson().toJson(this.history));
            // apply called asynchronously, calling commit would be done synchronously.
            // From my understanding, a second commit will wait for the first apply to finish.
            editor.apply();
        }
    }

    private void addToIndex(Points element) {
        // if index is in the middle of the list (which happens after undo),
        // remove everything after index
        index++;
        if (index < history.size()) {
            history.subList(index, history.size()).clear();
        }
        history.add(element);
        index = history.size() - 1;

        updateLocalStorage();
    }

    /**
     * Add new points object to game.
     *
     * @param p New life points
     */
    public void add(Points p) {
        Points current = getCurrentPoints();
        int p1 = p.p1;
        int p2 = p.p2;

        // if points are the same as before, ignore
        if(p1 == current.p1 && p2 == current.p2)
            return;

        p.setNames(current.getNames()[0], current.getNames()[1]);

        // if last entry less than <cooldown> ms ago, count as one action
        // also make sure, it's not the same player the points are subtracted from
        if (System.currentTimeMillis() - lastEntry <= COOLDOWN && (
                (p1 != current.p1 && p2 == current.p2 && lastEntryPlayer == 2) ||
                (p1 == current.p1 && p2 != current.p2 && lastEntryPlayer == 1)
        )) {
            history.set(index, p);
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

            addToIndex(p);
        }
    }

    /**
     * Add HistoryAction element. Action will be attached to current Points object.
     * See also {@link Points#addAction(HistoryAction)}.
     *
     * @param a History action
     */
    public void add(HistoryAction a) {
        getCurrentPoints().addAction(a);
        updateLocalStorage();
    }

    public long getAmountOfNewGames() {
        return history.stream()
                .filter(Points::isNewGame)
                .count();
    }

    public void removeOldestGame() {
        if(history.size() <= 1)
            return;

        int i = 1;
        while(i < (history.size() - 1) && !history.get(i).isNewGame()) {
            i++;
        }

        history.subList(0, i).clear();
        index -= i;
        updateLocalStorage();
    }

    public void removeNewGamesExcept4() {
        long ag = getAmountOfNewGames();
        while(ag >= 4) {
            removeOldestGame();
            ag--;
        }
    }

    private int lastNewGame() {
        int i = index;
        while(i >= 0 && !history.get(i).isNewGame()) {
            i--;
        }
        return i;
    }

    private int nextNewGame() {
        int i = index + 1;
        while(i < history.size() && !history.get(i).isNewGame()) {
            i++;
        }
        return i;
    }

    public void updateNames(String p1Name, String p2Name) {
        for(int i = Math.max(lastNewGame(), 0); i < nextNewGame(); i++) {
            history.get(i).setNames(p1Name, p2Name);
        }
        updateLocalStorage();
    }

    /**
     * Get the most current Point object in history.
     * <p>
     * History may be muddied with NewGame, Coin, Dice
     * or other HistoryElement objects, so to easily access the current life points for each
     * player, this method may be called.
     *
     * @return Most current Point object
     */
    public Points getCurrentPoints() {
        return history.get(index);
    }

    public Points get(int i) {
        return history.get(i);
    }

    public Points getMaxPoints() {
        return history.get(history.size()-1);
    }
    /**
     * Deletes everything in history, only keeps the most current points (see {@link #getCurrentPoints()}.
     */
    public void clearHistory() {
        if(isEmpty())
            return;

        Points p = getCurrentPoints();
        p.clearActions();
        history.clear();
        if(!p.isNewGame()) {
            if(p.p1 == GlobalOptions.getStartingLifePoints() && p.p2 == GlobalOptions.getStartingLifePoints()) {
                p.setNewGame(true);
            } else {
                history.add(new Points(GlobalOptions.getStartingLifePoints(), GlobalOptions.getStartingLifePoints(), true, p.getP1Name(), p.getP2Name()));
            }
        }
        history.add(p);
        index = history.size()-1;
        lastEntryPlayer = 0;
        lastEntry = 0;

        updateLocalStorage();
    }

    public boolean isEmpty() {
        return history.size() <= 1;
    }

    public void addNewGame() {
        Points pOld = getCurrentPoints();
        int l = GlobalOptions.getStartingLifePoints();
        if(pOld.isNewGame() && pOld.p1 == l && pOld.p2 == l)
            return;

        String[] names = pOld.getNames();
        Points p = new Points(l, l, true, names[0], names[1]);

        lastEntryPlayer = 0;
        lastEntry = 0;
        add(p);
    }

    public boolean hasActions() {
        for(Points action : this.history) {
            if(action.hasActions()) {
                return true;
            }
        }
        return false;
    }

    public Points undo() {
        if(canUndo())
            index--;
        return getCurrentPoints();
    }

    public Points redo() {
        if(canRedo())
            index++;
        return getCurrentPoints();
    }

    public boolean canUndo() {
        return index > 0;
    }

    public boolean canRedo() {
        return index < (history.size() - 1);
    }

    @NonNull
    @Override
    public Iterator<Points> iterator() {
        return history.iterator();
    }
}
