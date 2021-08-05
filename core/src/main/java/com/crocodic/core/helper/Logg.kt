package com.crocodic.core.helper

import android.util.Log
import com.crocodic.core.BuildConfig

object Logg {
    val dev = BuildConfig.DEBUG

    fun i(log: String, tag: String = BuildConfig.LIBRARY_PACKAGE_NAME) {
        if (dev) Log.i(tag, log)
    }

    fun e(log: String, tag: String = BuildConfig.LIBRARY_PACKAGE_NAME) {
        if (dev) Log.e(tag, log)
    }

    fun e(log: String, tr: Throwable, tag: String = BuildConfig.LIBRARY_PACKAGE_NAME) {
        if (dev) Log.e(tag, log, tr)
    }

    fun d(log: String, tag: String = BuildConfig.LIBRARY_PACKAGE_NAME) {
        if (dev) Log.d(tag, log)
    }

    fun v(log: String, tag: String = BuildConfig.LIBRARY_PACKAGE_NAME) {
        if (dev) Log.v(tag, log)
    }

    fun w(log: String, tag: String = BuildConfig.LIBRARY_PACKAGE_NAME) {
        if (dev) Log.w(tag, log)
    }
}