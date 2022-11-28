package at.kaasbrot.yugicalc.history;

import android.content.res.Resources;

import at.kaasbrot.yugicalc.R;


public class Dice implements HistoryAction {
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
    public String render(Resources res) {
        return res.getString(R.string.dice_lands_on) + " " + roll + ".";
    }

    public static String getDiceHtml(int roll) {
        switch (roll) {
            case 1: return "&#x2680;";
            case 2: return "&#x2681;";
            case 3: return "&#x2682;";
            case 4: return "&#x2683;";
            case 5: return "&#x2684;";
            case 6: return "&#x2685;";
            default: return "" + roll;
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
            default: return "" + roll;
        }
    }
}
