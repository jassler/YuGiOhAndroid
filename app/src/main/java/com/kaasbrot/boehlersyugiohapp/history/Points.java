package com.kaasbrot.boehlersyugiohapp.history;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

public class Points implements HistoryElement {
    public final int p1;
    public final int p2;

    public int diff1;
    public int diff2;

    public Points(int p1, int p2) {
        this(p1, p2, null);
    }

    public Points(int p1, int p2, Points prevPoints) {
        this.p1 = p1;
        this.p2 = p2;
        updateDiffs(prevPoints);
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

    public void updateDiffs(Points prevPoints) {
        if(prevPoints == null) {
            diff1 = diff2 = 0;
        } else {
            diff1 = p1 - prevPoints.p1;
            diff2 = p2 - prevPoints.p2;
        }
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);

        TextView p1Text = view.findViewById(R.id.p1);
        TextView p2Text = view.findViewById(R.id.p2);

        if(diff1 == 0 && diff2 == 0) {
            // assuming it's a new game, so make scores bold
            p1Text.setText(Html.fromHtml("<b>" + p1 + "</b>", Html.FROM_HTML_MODE_COMPACT));
            p2Text.setText(Html.fromHtml("<b>" + p2 + "</b>", Html.FROM_HTML_MODE_COMPACT));

        } else {
            p1Text.setText(Html.fromHtml(renderScore(p1, diff1), Html.FROM_HTML_MODE_COMPACT));
            p2Text.setText(Html.fromHtml(renderScore(p2, diff2), Html.FROM_HTML_MODE_COMPACT));
        }

        return view;
    }
}
