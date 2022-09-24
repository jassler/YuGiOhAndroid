package com.kaasbrot.boehlersyugiohapp.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaasbrot.boehlersyugiohapp.R;

public class Coins implements HistoryElement {
    public final int roll;

    public Coins(int roll) {
        this.roll = roll;
    }

    public String asHtml() {
        return getCoinsHtml(roll);
    }

    public String words() {
        return getCoinsWords(roll);
    }

    public String k() {
        return "K";
    }

    public String z() {
        return "Z";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coins coins = (Coins) o;
        return roll == coins.roll;
    }

    @Override
    public View render(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_history_element, parent, false);
        TextView text = view.findViewById(R.id.info);
        //text.setText(asUnicode());
        //text.setTextSize(32);
        text.setText(words());

        return view;
    }

    public static String getCoinsHtml(int roll) {
        switch (roll) {
            case 1: return "&#x24DA;"+"<br>"+"&#x24DA;"+"<br>"+"&#x24DA;";
            case 2: return "&#x24DA;"+"<br>"+"&#x24DA;"+"<br>"+"&#x24E9;";
            case 3: return "&#x24DA;"+"<br>"+"&#x24E9;"+"<br>"+"&#x24DA;";
            case 4: return "&#x24DA;"+"<br>"+"&#x24E9;"+"<br>"+"&#x24E9;";
            case 5: return "&#x24E9;"+"<br>"+"&#x24DA;"+"<br>"+"&#x24DA;";
            case 6: return "&#x24E9;"+"<br>"+"&#x24DA;"+"<br>"+"&#x24E9;";
            case 7: return "&#x24E9;"+"<br>"+"&#x24E9;"+"<br>"+"&#x24DA;";
            case 8: return "&#x24E9;"+"<br>"+"&#x24E9;"+"<br>"+"&#x24E9;";
            default: return "Unknown Unicode for " + roll;
        }
    }

    public static String getCoinsWords(int roll) {
        switch (roll) {
            case 1: return "Kopf, Kopf, Kopf";
            case 2: return "Kopf, Kopf, Zahl";
            case 3: return "Kopf, Zahl, Kopf";
            case 4: return "Kopf, Zahl, Zahl";
            case 5: return "Zahl, Kopf, Kopf";
            case 6: return "Zahl, Kopf, Zahl";
            case 7: return "Zahl, Zahl, Kopf";
            case 8: return "Zahl, Zahl, Zahl";
            default: return "Unknown Unicode for " + roll;
        }
    }
}
