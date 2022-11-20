package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.MainActivity;
import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.Points;

public class HistoryDialog extends AppCompatDialogFragment {

    MainActivity activity;
    History history;

    public void setHistory(History history) {
        this.history = history;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View parentView = inflater.inflate(R.layout.dialog_history, null);
        LinearLayout parent = parentView.findViewById(R.id.historyLayout);

        Resources res = getResources();

        Points prev = new Points(0, 0);

        for (Points p : history) {
            if(p.isNewGame()) {
                parent.addView(
                        inflater.inflate(R.layout.dialog_history_line, parent, false)
                );
            }

            View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
            ((TextView) view.findViewById(R.id.p1)).setText(Html.fromHtml(p.renderP1(prev), Html.FROM_HTML_MODE_COMPACT));
            ((TextView) view.findViewById(R.id.p2)).setText(Html.fromHtml(p.renderP2(prev), Html.FROM_HTML_MODE_COMPACT));
            ((TextView) view.findViewById(R.id.info)).setText(Html.fromHtml(p.renderActions(res), Html.FROM_HTML_MODE_COMPACT));
            parent.addView(view);

            prev = p;
        }

        ScrollView scroll = parentView.findViewById(R.id.historyScroll);
        scroll.post(() -> {
            scroll.setSmoothScrollingEnabled(false);
            scroll.fullScroll(View.FOCUS_DOWN);
            scroll.setSmoothScrollingEnabled(true);
        });

        builder
                .setView(parentView)
                .setTitle(R.string.show_history)
                .setPositiveButton("ok", (dialogInterface, i) -> {})
                .setNegativeButton(R.string.clear_text, ((dialogInterface, i) -> {
                    if(history != null) history.clearHistory();
                    if(activity != null) activity.determineButtonEnable();
                }))
        ;
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
