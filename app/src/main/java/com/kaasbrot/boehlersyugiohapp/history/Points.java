package com.kaasbrot.boehlersyugiohapp.history;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

import android.content.res.Resources;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Points {

    private boolean isNewGame;
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
        this.p1 = p1;
        this.p2 = p2;
        this.isNewGame = isNewGame;
        this.actions = null;
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

    public void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    public boolean isNewGame() {
        return isNewGame;
    }

    public String renderP1(Points prev) {
        if(isNewGame) {
            return "<b>" + p1 + "</b>";
        } else {
            return renderScore(p1, p1 - prev.p1);
        }
    }

    public String renderP2(Points prev) {
        if(isNewGame) {
            return "<b>" + p2 + "</b>";
        } else {
            return renderScore(p2, p2 - prev.p2);
        }
    }

    public String renderActions(Resources res) {
//        String pre = isNewGame ? ("<i>" + res.getString(R.string.new_game) + "</i><br><br>") : "<br><br>";

        if(actions == null) {
            return "";
        } else {
//            return pre + actions.stream()
            return (isNewGame ? "<br>" : "<br><br>") + actions.stream()
                    .map(x -> x.render(res))
                    .collect(Collectors.joining("<br>"));
        }
    }

    public void clearActions() {
        this.actions = null;
    }
}
