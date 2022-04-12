package com.crocodic.core.api

import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by @yzzzd on 4/22/18.
 */

class ApiObserver(
    block: suspend () -> String,
    dispatcher: CoroutineDispatcher,
    toast: Boolean = false,
    responseListener: ResponseListener
) {

    constructor(
        block: suspend () -> String,
        toast: Boolean = false,
        responseListener: ResponseListener
    ) : this(block, Dispatchers.IO, toast, responseListener)

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

/**
 * @param block suspend function
 * @param dispatcher Default [Dispatchers.IO]
 * @param toast Default false
 * @param listener Anonymus class to handle response
 */
suspend fun apiObserver(
    block: suspend () -> String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    toast: Boolean = false,
    listener: ResponseListener
) {
    try {
        val responseJSON = withContext(dispatcher) {
            val response = block.invoke()
            JSONObject(response)
        }
        listener.onSuccess(responseJSON)
    } catch (t: Throwable) {
        val response = ApiResponse(isToast = toast).responseError(t)
        listener.onError(response)
    }
}

interface ResponseListener {
    suspend fun onSuccess(response: JSONObject)

    suspend fun onError(response: ApiResponse) {
        EventBus.getDefault().post(response)
    }
}