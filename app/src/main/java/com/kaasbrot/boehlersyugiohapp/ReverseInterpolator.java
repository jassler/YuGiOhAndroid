package com.kaasbrot.boehlersyugiohapp;

import android.view.animation.Interpolator;

public class ReverseInterpolator implements Interpolator {

    private final Interpolator other;
    private boolean isReversed;

    public ReverseInterpolator(Interpolator other, boolean reversed) {
        this.other = other;
        this.isReversed = reversed;
    }

    public void setReversed(boolean reversed) {
        this.isReversed = reversed;
    }

    @Override
    public float getInterpolation(float input) {
        if(isReversed) {
            return 1f - other.getInterpolation(input);
        } else {
            return other.getInterpolation(input);
        }
    }
}
