package com.crocodic.core.api

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by @yzzzd on 4/22/18.
 */

class ApiObserver(block: suspend () -> String, toast: Boolean = false, responseListener: ResponseListener) {
    init {
        val exception = CoroutineExceptionHandler { coroutineContext, throwable ->
            val response = ApiResponse(isToast = toast).responseError(throwable)
            responseListener.onError(response)
        }

        CoroutineScope(exception + Dispatchers.IO).launch {
            val response = block()
            val responseJson = JSONObject(response)
            responseListener.onSuccess(responseJson)
        }
    }

    interface ResponseListener {
        fun onSuccess(response: JSONObject)

        fun onError(response: ApiResponse) {
            EventBus.getDefault().post(response)
        }
    }
}