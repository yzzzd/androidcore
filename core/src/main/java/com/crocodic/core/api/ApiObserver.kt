package com.crocodic.core.api

import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by @yzzzd on 4/22/18.
 */

class ApiObserver(block: suspend () -> String, dispatcher: CoroutineDispatcher, toast: Boolean = false, responseListener: ResponseListener) {

    constructor(block: suspend () -> String, toast: Boolean = false, responseListener: ResponseListener): this(block, Dispatchers.IO, toast, responseListener)

    init {
        val exception = CoroutineExceptionHandler { coroutineContext, throwable ->
            val response = ApiResponse(isToast = toast).responseError(throwable)
            responseListener.onError(response)
        }

        CoroutineScope(exception + dispatcher).launch {
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