package com.crocodic.core.helper.log

import android.util.Log
import com.crocodic.core.BuildConfig

/**
 * Created by @yzzzd on 4/22/18.
 */

object Log {
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