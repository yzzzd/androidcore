package com.nuryazid.androidcore.ui.main

import android.os.Bundle
import com.nuryazid.androidcore.R
import com.nuryazid.androidcore.base.BaseActivity
import com.nuryazid.androidcore.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}