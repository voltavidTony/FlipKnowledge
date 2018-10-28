package com.gmail.anthonyplugins.flipknowledge;

import android.view.animation.Interpolator;

class ReverseLinearInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return 1.0f - input;
    }
}

