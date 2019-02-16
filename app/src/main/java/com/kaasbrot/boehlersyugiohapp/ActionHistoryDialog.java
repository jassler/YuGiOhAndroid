package com.kaasbrot.boehlersyugiohapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class ActionHistoryDialog extends AppCompatDialogFragment {

    ActionHistory history;

    private final String FONT_NEGATIVE = "<font color=\"#CC0000\"><i>";
    private final String FONT_POSITIVE = "<font color=\"#00AA00\"><i>";

    private static final String FONT_NEGATIVE_END = "</i></font>";
    private static final String FONT_POSITIVE_END = "</i></font>";

    public void setHistory(ActionHistory history) {
        this.history = history;
    }

    private void appendScore(StringBuilder sb, int scoreDiff) {
        if (scoreDiff > 0) {
            sb.append("<br><br>")
                    .append(FONT_POSITIVE)
                    .append('+')
                    .append(scoreDiff)
                    .append(FONT_POSITIVE_END)
                    .append("<br>");

        } else if (scoreDiff < 0) {
            sb.append("<br><br>")
                    .append(FONT_NEGATIVE)
                    .append(scoreDiff)
                    .append(FONT_NEGATIVE_END)
                    .append("<br>");

        } else {
            sb.append("<br><br><br>");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_history, null);
        builder.setView(view).setTitle(R.string.show_history).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        StringBuilder p1Str = new StringBuilder("8000");
        StringBuilder p2Str = new StringBuilder("8000");

        List<ActionHistory.Points> p = history.getHistory();

        Iterator<ActionHistory.Points> it = p.iterator();
        if (it.hasNext()) {
            ActionHistory.Points prev = it.next();

            while (it.hasNext()) {
                ActionHistory.Points curr = it.next();

                int diffP1 = curr.p1 - prev.p1;
                int diffP2 = curr.p2 - prev.p2;

                appendScore(p1Str, diffP1);
                appendScore(p2Str, diffP2);

                if (!it.hasNext()) {
                    p1Str.append("<b><font color=\"#000000\">");
                    p2Str.append("<b><font color=\"#000000\">");
                }

                p1Str.append(curr.p1);
                p2Str.append(curr.p2);

                prev = curr;
            }
        }


        TextView p1Text = view.findViewById(R.id.historyTextP1);
        TextView p2Text = view.findViewById(R.id.historyTextP2);

        p1Text.setText(Html.fromHtml(p1Str.toString(), Html.FROM_HTML_MODE_COMPACT));
        p2Text.setText(Html.fromHtml(p2Str.toString(), Html.FROM_HTML_MODE_COMPACT));

        ScrollView scrollView = view.findViewById(R.id.historyScroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        /*LinearLayout linearView = view.findViewById(R.id.linearList);

        if(history != null) {
            for (ActionHistory.Points points : history.getHistory()) {
                LayoutInflater li = getActivity().getLayoutInflater();
                View v = li.inflate(R.layout.dialog_history_element, null);

                TextView p1 = v.findViewById(R.id.historyPointsP1);
                TextView p2 = v.findViewById(R.id.historyPointsP2);

                p1.setText(String.valueOf(points.p1));
                p2.setText(String.valueOf(points.p2));

                linearView.addView(v);
            }
        }*/

        return builder.create();
    }
}
