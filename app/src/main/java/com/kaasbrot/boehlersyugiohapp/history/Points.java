package com.kaasbrot.boehlersyugiohapp.history;

import android.content.res.Resources;

import com.kaasbrot.boehlersyugiohapp.R;

import java.util.ArrayList;
import java.util.List;

public class Points {

    private boolean isNewGame;
    private String p1Name;
    private String p2Name;
    public final int p1;
    public final int p2;

    //public SpannableString smallbreak = new SpannableString("<br>");
    //public SpannableString smallbreak2 = smallbreak.setSpan(new RelativeSizeSpan(8dp), 0, 1);
            //.setSpan(new AbsoluteSizeSpan(textSize1), 0, text1.length(), SPAN_INCLUSIVE_INCLUSIVE);
    private List<HistoryAction> actions;

    public Points(int pointsForBoth) {
        this(pointsForBoth, pointsForBoth);
    }

    public Points(int p1, int p2) {
        this(p1, p2, false);
    }

    public Points(int p1, int p2, boolean isNewGame) {
        this(p1, p2, isNewGame, "", "");
    }

    public Points(int p1, int p2, boolean isNewGame, String p1Name, String p2Name) {
        this.p1 = p1;
        this.p2 = p2;
        this.isNewGame = isNewGame;
        this.actions = null;
        this.p1Name = (p1Name == null) ? "" : p1Name;
        this.p2Name = (p2Name == null) ? "" : p2Name;
    }

    private String renderScore(int score, int diff) {
        if (diff > 0) {
            return "<font color=\"#00AA00\"><i>+" + diff + "</i></font><br>" + score;
        } else if (diff < 0) {
            return "<font color=\"#CC0000\"><i>" + diff + "</i></font><br>" + score;
        } else {
            return "<br>" + score;
        }
    }

    public void addAction(HistoryAction a) {
        if(actions == null)
            actions = new ArrayList<>();
        actions.add(a);
    }

    public void setNames(String p1Name, String p2Name) {
        this.p1Name = p1Name;
        this.p2Name = p2Name;
    }

    public String[] getNames() {
        return new String[]{ this.p1Name, this.p2Name };
    }

    public void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    public boolean isNewGame() {
        return isNewGame;
    }

    public String renderP1(Points prev) {
        if(isNewGame) {
            return "<br><br><b>" + p1 + "</b>";
        } else {
            return renderScore(p1, p1 - prev.p1);
        }
    }

    public String renderP2(Points prev) {
        if(isNewGame) {
            return "<br><br><b>" + p2 + "</b>";
        } else {
            return renderScore(p2, p2 - prev.p2);
        }
    }

    public String renderActions(Resources res, boolean showActions) {
//        String pre = isNewGame ? ("<i>" + res.getString(R.string.new_game) + "</i><br><br>") : "<br><br>";
        StringBuilder s = new StringBuilder();
        if(isNewGame) {
            s.append("<b>");
            if(p1Name.isEmpty()) s.append(res.getText(R.string.playername1));
            else s.append(p1Name);

            s.append("</b> <i>vs</i> <b>");
            if(p2Name.isEmpty()) s.append(res.getText(R.string.playername2));
            else s.append(p2Name);

            s.append("</b><br>");
        }

        if(actions != null && showActions) {
            s.append("<br>");
            actions.forEach(action -> s.append("<br>").append(action.render(res)));
        }

        return s.toString();
    }

    public void clearActions() {
        this.actions = null;
    }
}
