package com.kaasbrot.boehlersyugiohapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

public class Dice implements HistoryElement {
    public final int roll;

    public Dice(int roll) {
        this.roll = roll;
    }

    public String asHtml() {
        return getDiceHtml(roll);
    }

    public String asUnicode() {
        return getDiceUnicode(roll);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dice dice = (Dice) o;
        return roll == dice.roll;
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
        TextView text = view.findViewById(R.id.info);
        //text.setText(asUnicode());
        //text.setTextSize(32);
        text.setText("Würfel landete auf "+roll+".");

        return view;
    }

    public static String getDiceHtml(int roll) {
        switch (roll) {
            case 1: return "&#x2680;";
            case 2: return "&#x2681;";
            case 3: return "&#x2682;";
            case 4: return "&#x2683;";
            case 5: return "&#x2684;";
            case 6: return "&#x2685;";
            default: return "Unknown Unicode for " + roll;
        }
    }

    public static String getDiceUnicode(int roll) {
        switch (roll) {
            case 1: return "\u2680";
            case 2: return "\u2681";
            case 3: return "\u2682";
            case 4: return "\u2683";
            case 5: return "\u2684";
            case 6: return "\u2685";
            default: return "Unknown Unicode for " + roll;
        }
    }
}
