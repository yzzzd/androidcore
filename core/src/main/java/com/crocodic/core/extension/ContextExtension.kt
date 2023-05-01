package com.crocodic.core.extension

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.drawableRes(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)
fun Context.colorRes(@ColorRes id: Int) = ContextCompat.getColor(this, id)