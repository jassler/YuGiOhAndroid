package com.kaasbrot.boehlersyugiohapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

public class NewGame implements HistoryElement {

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
        TextView text = view.findViewById(R.id.info);
        //text.setText(String.valueOf(toss.unicode));
        text.setText("Neues Spiel");
        text.setFontFeatureSettings("Italics");
        //text.setTextSize(32);

        return view;
    }
}
