package com.gmail.anthonyplugins.flipknowledge;

import android.view.animation.Interpolator;

class ReverseOffsetInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return (1.5f - input) % 1.0f;
    }
}
