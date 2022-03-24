package com.crocodic.core.helper.log

import timber.log.Timber

/**
 * Created by @yzzzd on 4/22/18.
 */

object Log {
    fun i(message: String) = Timber.i(message)

    fun e(message: String) = Timber.e(message)

    fun e(message: String, tr: Throwable) = Timber.e(tr, message)

    fun d(message: String) = Timber.d(message)

    fun v(message: String) = Timber.v(message)

    fun w(message: String) = Timber.w(message)
}