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
    public final PointBefore prev;


    public Points(int p1, int p2, PointBefore prev) {
        this.p1 = p1;
        this.p2 = p2;
        this.prev = prev;
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

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);

        TextView p1Text = view.findViewById(R.id.p1);
        TextView p2Text = view.findViewById(R.id.p2);

        Points before = null;
        if (prev != null) {
            before = prev.before(this);
        }

        if(before == null) {
            p1Text.setText(Html.fromHtml("<b>" + p1 + "</b>", Html.FROM_HTML_MODE_COMPACT));
            p2Text.setText(Html.fromHtml("<b>" + p2 + "</b>", Html.FROM_HTML_MODE_COMPACT));

        } else {
            p1Text.setText(Html.fromHtml(renderScore(p1, p1 - before.p1), Html.FROM_HTML_MODE_COMPACT));
            p2Text.setText(Html.fromHtml(renderScore(p2, p2 - before.p2), Html.FROM_HTML_MODE_COMPACT));
        }

        return view;
    }
}
