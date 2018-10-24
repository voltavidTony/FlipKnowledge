package com.gmail.anthonyplugins.flipknowledge;

import android.view.animation.Interpolator;

public class OffsetInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        float pos = input + 0.5f;
        return Math.abs(pos - (float) Math.floor(pos));
    }
}
