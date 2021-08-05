package com.crocodic.core.helper

import android.content.res.Resources

/**
 * Created by @yzzzd on 11/30/17 10:05 AM.
 */

object Dpx {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun pxToSp(px: Float): Int {
        return (px / Resources.getSystem().displayMetrics.scaledDensity).toInt()
    }
}
