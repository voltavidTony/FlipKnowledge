package com.gmail.anthonyplugins.flipknowledge;

import android.view.animation.Interpolator;

public class ReverseOffsetInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        float pos = 0.5f - input;
        return Math.abs(pos - (float) Math.floor(pos));
    }
}
