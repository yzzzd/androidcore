package com.nuryazid.androidcore.ui.main

import android.location.Location
import android.os.Bundle
import com.crocodic.core.extension.checkLocationPermission
import com.crocodic.core.helper.log.Log
import com.nuryazid.androidcore.R
import com.nuryazid.androidcore.base.BaseActivity
import com.nuryazid.androidcore.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLocationPermission {
            listenLocationChange()
        }
    }

    override fun retrieveLocationChange(location: Location) {
        Log.d("retrieve location at ${location.accuracy}")
    }
}