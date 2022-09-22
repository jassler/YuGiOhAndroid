package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.HistoryElement;

import java.util.Objects;

public class ActionHistoryDialog extends AppCompatDialogFragment {

    History history;

//    private final String FONT_NEGATIVE = "<font color=\"#CC0000\"><i>";
//    private final String FONT_POSITIVE = "<font color=\"#00AA00\"><i>";
//    private final String FONT_EMPTY = "<font color=\"#FFFFFF\"><i>";
//
//    private static final String FONT_NEGATIVE_END = "</i></font>";
//    private static final String FONT_POSITIVE_END = "</i></font>";
//    private static final String FONT_EMPTY_END = "</i></font>";

    public void setHistory(History history) {
        this.history = history;
    }

//    private void appendScore(StringBuilder sb, int scoreDiff) {
//        if (scoreDiff > 0) {
//            sb.append("<br><br>")
//                    .append(FONT_POSITIVE)
//                    .append('+')
//                    .append(scoreDiff)
//                    .append(FONT_POSITIVE_END)
//                    .append("<br>");
//
//        } else if (scoreDiff < 0) {
//            sb.append("<br><br>")
//                    .append(FONT_NEGATIVE)
//                    .append(scoreDiff)
//                    .append(FONT_NEGATIVE_END)
//                    .append("<br>");
//
//        } else {
//            sb.append("<br><br>")
//                    .append(FONT_EMPTY)
//                    .append("empty")
//                    .append(FONT_EMPTY_END)
//                    .append("<br>");
//        }
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View parentView = inflater.inflate(R.layout.dialog_history, null);
        LinearLayout parent = parentView.findViewById(R.id.historyLayout);

        for (HistoryElement element : history.getHistory()) {
            parent.addView(element.render(inflater, parent));
        }

        builder.setView(parentView).setTitle(R.string.show_history).setPositiveButton("ok", (dialogInterface, i) -> {});
        return builder.create();

//        IN CASE WE WANNA GO BACK TO TEXT-ONLY
//        View view = inflater.inflate(R.layout.dialog_history, null);
//        builder.setView(view).setTitle(R.string.show_history).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//
//
//        StringBuilder p1Str = new StringBuilder("8000");
//        StringBuilder p2Str = new StringBuilder("8000");
//
//        List<History.Points> p = history.getHistory();
//
//        Iterator<History.Points> it = p.iterator();
//        if (it.hasNext()) {
//            History.Points prev = it.next();
//
//            while (it.hasNext()) {
//                History.Points curr = it.next();
//
//                int diffP1 = curr.p1 - prev.p1;
//                int diffP2 = curr.p2 - prev.p2;
//
//                appendScore(p1Str, diffP1);
//                appendScore(p2Str, diffP2);
//
//                if (!it.hasNext()) {
//                    p1Str.append("<b><font color=\"#000000\">");
//                    p2Str.append("<b><font color=\"#000000\">");
//                }
//
//                p1Str.append(curr.p1);
//                p2Str.append(curr.p2);
//
//                prev = curr;
//            }
//        }
//
//
//        TextView p1Text = view.findViewById(R.id.historyTextP1);
//        TextView p2Text = view.findViewById(R.id.historyTextP2);
//
//        p1Text.setText(Html.fromHtml(p1Str.toString(), Html.FROM_HTML_MODE_COMPACT));
//        p2Text.setText(Html.fromHtml(p2Str.toString(), Html.FROM_HTML_MODE_COMPACT));
//
//        ScrollView scrollView = view.findViewById(R.id.historyScroll);
//        scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
    }
}
