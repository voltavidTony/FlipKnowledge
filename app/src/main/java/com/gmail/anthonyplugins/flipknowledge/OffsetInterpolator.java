package com.gmail.anthonyplugins.flipknowledge;

import android.view.animation.Interpolator;

class OffsetInterpolator implements Interpolator {

    private boolean reverse;

    OffsetInterpolator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public float getInterpolation(float input) {
        if (reverse) return (1.5f - input) % 1.0f;
        else return (1.5f + input) % 1.0f;
    }
}
