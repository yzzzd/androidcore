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

open class ApiObserver(block: suspend () -> String, toast: Boolean = false, onSuccess: (response: JSONObject) -> Unit) {

    private var onErrorThrowable: ((ApiResponse) -> Unit)? = null

    init {
        val exception = CoroutineExceptionHandler { coroutineContext, throwable ->
            val response = ApiResponse(isToast = toast).responseError(throwable)
            EventBus.getDefault().post(response)
            onErrorThrowable?.invoke(response)
        }

        CoroutineScope(exception + Dispatchers.IO).launch {
            val response = block()
            val responseJson = JSONObject(response)
            onSuccess(responseJson)
        }
    }

    fun onError(onError: (response: ApiResponse) -> Unit) {
        onErrorThrowable = onError
    }
}