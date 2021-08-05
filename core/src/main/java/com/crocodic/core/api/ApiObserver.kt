package com.crocodic.core.api

import io.reactivex.observers.DisposableSingleObserver
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

open class ApiObserver(private val isToast: Boolean = false): DisposableSingleObserver<String>() {
    override fun onSuccess(t: String) {
        if (isToast) {
            val responseJson = JSONObject(t)

            val apiStatus = responseJson.getInt(ApiCode.STATUS)
            val apiMessage = responseJson.getString(ApiCode.MESSAGE)

            EventBus.getDefault().post(ApiResponse().response(apiStatus, apiMessage, if (isToast) 1 else null))
        }
    }

    override fun onError(e: Throwable) {
        EventBus.getDefault().post(ApiResponse().responseError(e, if (isToast) 1 else null))
    }
}