package com.nuryazid.androidcore.base

import android.app.Application
import com.crocodic.core.helper.tree.ReleaseTree
import com.nuryazid.androidcore.BuildConfig
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}