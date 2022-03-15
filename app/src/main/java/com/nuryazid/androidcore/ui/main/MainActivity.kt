package com.nuryazid.androidcore.ui.main

import android.location.Location
import android.os.Bundle
import android.util.Log
import com.crocodic.core.extension.checkLocationPermission
import com.nuryazid.androidcore.R
import com.nuryazid.androidcore.base.BaseActivity
import com.nuryazid.androidcore.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLocationPermission {
            listenLocationChange()
        }
    }

    override fun retrieveLocationChange(location: Location) {
        Log.d("sample", "retreive location at ${location.accuracy}")
    }
}