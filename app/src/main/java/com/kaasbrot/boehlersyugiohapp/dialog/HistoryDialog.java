package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.GlobalOptions;
import com.kaasbrot.boehlersyugiohapp.MainActivity;
import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.Points;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class HistoryDialog extends AppCompatDialogFragment {

    MainActivity activity;
    History history;

    private final List<View> historyElements = new ArrayList<>();

    public void setHistory(History history) {
        this.history = history;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        historyElements.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        View view = constructView();

        builder
                .setView(view)
                .setTitle(R.string.show_history)
                .setPositiveButton("ok", (dialogInterface, i) -> {})
                .setNegativeButton(R.string.clear_text, (dialogInterface, i) -> promptClearHistory())
                .setNeutralButton(GlobalOptions.isActionsShown() ?
                        R.string.history_hide_actions :
                        R.string.history_show_actions,
                        (dialog, which) -> {}
                )
        ;
        return builder.create();
    }

    private void promptClearHistory(){
        new AlertDialog.Builder(this.getContext(), R.style.MyDialogTheme)
                .setTitle(R.string.clear_history)
                .setMessage(R.string.are_you_sure)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, ((dialogInterface, i) -> {
                    if(history != null) history.clearHistory();
                    if(activity != null) activity.determineButtonEnable();
                })
                ).create().show();
    }

    private View constructView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View parentView = inflater.inflate(R.layout.dialog_history, null);
        LinearLayout parent = parentView.findViewById(R.id.historyLayout);

        Resources res = getResources();

        Points prev = history.get(0);
        Points current = history.getCurrentPoints();
        boolean showActions = GlobalOptions.isActionsShown();

        for (Points p : history) {
            if(p.isNewGame()) {
                View lineview = inflater.inflate(R.layout.dialog_history_line, parent, false);
                if(GlobalOptions.isShowNames()==false) {
                    ((TextView) lineview.findViewById(R.id.line_p1)).setTextSize(0);
                    ((TextView) lineview.findViewById(R.id.line_p2)).setTextSize(0);
                }
                String p1name = p.getP1Name();
                String p2name = p.getP2Name();
                if(p1name.isEmpty()) p1name = getResources().getString(R.string.playername1);
                if(p2name.isEmpty()) p2name = getResources().getString(R.string.playername2);
                ((TextView) lineview.findViewById(R.id.line_p1)).setText(p1name);
                ((TextView) lineview.findViewById(R.id.line_p2)).setText(p2name);
                parent.addView(lineview);
            }

            View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
            ((TextView) view.findViewById(R.id.p1)).setText(Html.fromHtml(p.renderP1(prev), Html.FROM_HTML_MODE_COMPACT));
            ((TextView) view.findViewById(R.id.p2)).setText(Html.fromHtml(p.renderP2(prev), Html.FROM_HTML_MODE_COMPACT));
            ((TextView) view.findViewById(R.id.info)).setText(Html.fromHtml(p.renderActions(res, showActions), Html.FROM_HTML_MODE_COMPACT));
            if(p == current && p != history.getMaxPoints())
                view.setBackgroundColor(Color.parseColor("#323232"));

            parent.addView(view);
            historyElements.add(view);

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
                .setNeutralButton(GlobalOptions.isActionsShown() ?
                                R.string.history_hide_actions :
                                R.string.history_show_actions,
                        (dialog, which) -> {}
                )
        ;

        return parentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Button neutralButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(b -> {
            boolean isShown = !GlobalOptions.isActionsShown();
            GlobalOptions.setActionsShown(isShown);
            Resources res = getResources();

            for (int i = 0; i < historyElements.size(); i++) {
                TextView info = historyElements.get(i).findViewById(R.id.info);
                info.setText(Html.fromHtml(history.get(i).renderActions(res, isShown), Html.FROM_HTML_MODE_COMPACT));
            }
            if(isShown) {
                ScrollView scroll = getDialog().findViewById(R.id.historyScroll);
                scroll.post(() -> {
//                    scroll.setSmoothScrollingEnabled(false);
                    scroll.fullScroll(View.FOCUS_DOWN);
//                    scroll.setSmoothScrollingEnabled(true);
                });
                neutralButton.setText(R.string.history_hide_actions);
            } else {
                neutralButton.setText(R.string.history_show_actions);
            }
        });
    }
}
