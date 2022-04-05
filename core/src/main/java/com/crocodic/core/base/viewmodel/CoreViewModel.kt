package com.crocodic.core.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.activity.CoreActivity
import org.greenrobot.eventbus.EventBus

abstract class CoreViewModel : ViewModel() {

    val apiResponse = MutableLiveData<ApiResponse>()

    abstract fun apiRenewToken()

    abstract fun apiLogout()

    protected fun tokenSuccess() {
        EventBus.getDefault().post(CoreActivity.Companion.EVENT.RENEW_TOKEN)
    }

    protected fun logoutSuccess() {
        EventBus.getDefault().post(CoreActivity.Companion.EVENT.LOGGED_OUT)
    }
}