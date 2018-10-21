package com.gmail.anthonyplugins.flipknowledge;

import android.view.animation.Interpolator;

public class ReverseLinearInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return Math.abs(input - 1f);
    }
}

