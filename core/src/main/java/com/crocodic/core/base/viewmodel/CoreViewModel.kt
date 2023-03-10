package com.crocodic.core.base.viewmodel

import androidx.lifecycle.ViewModel
import com.crocodic.core.api.ApiResponse
import com.crocodic.core.base.activity.CoreActivity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.greenrobot.eventbus.EventBus

abstract class CoreViewModel : ViewModel() {

    protected val _apiResponse = MutableSharedFlow<ApiResponse>() // private mutable shared flow
    val apiResponse = _apiResponse.asSharedFlow() // publicly exposed as read-only shared flow

    protected var apa: String? = null

    abstract fun apiRenewToken()

    abstract fun apiLogout()

    protected fun tokenSuccess() {
        EventBus.getDefault().post(CoreActivity.Companion.EVENT.RENEW_TOKEN)
    }

    protected fun logoutSuccess() {
        EventBus.getDefault().post(CoreActivity.Companion.EVENT.LOGGED_OUT)
    }
}