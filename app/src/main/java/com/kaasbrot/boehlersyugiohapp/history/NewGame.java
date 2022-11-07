package com.kaasbrot.boehlersyugiohapp.history;

import androidx.core.content.ContextCompat;
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
        text.setText(R.string.new_game);
        text.setFontFeatureSettings("Italics");

        view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.border_bottom_2));

        return view;
    }
}
