package com.infinix.instalane.utils

import android.view.animation.Interpolator

class JellyInterpolator : Interpolator {


    override fun getInterpolation(t: Float): Float {
        val delta = t - BOUNCE_APEX_MOMENT
        if (t < BOUNCE_APEX_MOMENT) {
            return SQUARE_COEFFICIENT * delta * delta + BOUNCE_APEX
        } else {
            if (delta == 0f) {
                return 1f
            } else {
                val upperBound = 1 - BOUNCE_APEX_MOMENT
                val subinterpolation = (1.5 + (BOUNCE_FACTOR - 1) * 2) * Math.PI * delta.toDouble() / upperBound
                val minifier = Math.pow((upperBound / (upperBound - delta)).toDouble(), 1.0)
                return (1 + BOUNCE_ADDITION * Math.cos(subinterpolation) / minifier).toFloat()
            }
        }

    }

    companion object {

        private val BOUNCE_FACTOR = 1f
        private val BOUNCE_APEX_MOMENT = 0.7f
        private val BOUNCE_ADDITION = 0.03f

        private val BOUNCE_APEX = 1 + BOUNCE_ADDITION
        private val SQUARE_COEFFICIENT = -BOUNCE_APEX / (BOUNCE_APEX_MOMENT * BOUNCE_APEX_MOMENT)
    }
}