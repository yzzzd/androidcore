package com.crocodic.core.extension

import android.graphics.Color
import androidx.annotation.ColorInt

val String.asColor: Int @ColorInt get() = Color.parseColor(this)