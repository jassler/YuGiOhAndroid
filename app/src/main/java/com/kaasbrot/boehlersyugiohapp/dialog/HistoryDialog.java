package com.kaasbrot.boehlersyugiohapp.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kaasbrot.boehlersyugiohapp.R;
import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.HistoryElement;

public class HistoryDialog extends AppCompatDialogFragment {

    History history;

    public void setHistory(History history) {
        this.history = history;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View parentView = inflater.inflate(R.layout.dialog_history, null);
        LinearLayout parent = parentView.findViewById(R.id.historyLayout);

        for (HistoryElement element : history.getHistory()) {
            parent.addView(element.render(inflater, parent));
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
                .setNegativeButton(R.string.clear_text, ((dialogInterface, i) -> history.clearHistory()))
        ;
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
