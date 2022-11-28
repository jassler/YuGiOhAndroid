package at.kaasbrot.yugicalc.history;

import android.content.res.Resources;

import at.kaasbrot.yugicalc.R;


public class Coin implements HistoryAction {
    public enum Toss {
        HEADS('\u2461', "&#x24DA;" ),
        TAILS('\u24D7', "&#x24E9;" );

        public final char unicode;
        public final String html;
        Toss(char c, String html) {
            this.unicode = c;
            this.html = html;
        }
    }

    public final Toss toss;

    public Coin(Toss toss) {
        this.toss = toss;
    }

    public Coin(boolean isHeads) {
        this(isHeads ? Toss.HEADS : Toss.TAILS);
    }

//    public int tossDrawable() {
//        switch(toss) {
//            case HEADS: return R.drawable.heads_aa;
//            case TAILS: return R.drawable.tails_aa;
//            default: throw new RuntimeException("Unknown Toss value " + toss);
//        }
//    }

    @Override
    public String render(Resources res) {
        int pre = R.string.coin_lands_on;
        int post = (toss == Toss.HEADS) ? R.string.result_heads : R.string.result_tails;
        return res.getString(pre) + " " + res.getString(post) + ".";
    }
}
