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
    public final PointNext next;


    public Points(int p1, int p2, PointNext next) {
        this.p1 = p1;
        this.p2 = p2;
        this.next = next;
    }

    private String renderScore(int score, int diff) {
        if (diff > 0) {
            return score + "<br><font color=\"#00AA00\"><i>+" + diff + "</i></font>";
        } else if (diff < 0) {
            return score + "<br><font color=\"#CC0000\"><i>" + diff + "</i></font>";
        } else {
            return String.valueOf(score);
        }
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);

        TextView p1Text = view.findViewById(R.id.p1);
        TextView p2Text = view.findViewById(R.id.p2);

        Points after = null;
        if (next != null) {
            after = next.after(this);
        }

        if(after == null) {
            p1Text.setText(Html.fromHtml("<b>" + p1 + "</b>", Html.FROM_HTML_MODE_COMPACT));
            p2Text.setText(Html.fromHtml("<b>" + p2 + "</b>", Html.FROM_HTML_MODE_COMPACT));

        } else {
            p1Text.setText(Html.fromHtml(renderScore(p1, after.p1 - p1), Html.FROM_HTML_MODE_COMPACT));
            p2Text.setText(Html.fromHtml(renderScore(p2, after.p2 - p2), Html.FROM_HTML_MODE_COMPACT));
        }

        return view;
    }
}
