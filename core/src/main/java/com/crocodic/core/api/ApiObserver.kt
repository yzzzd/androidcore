package com.crocodic.core.api

import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * Created by @yzzzd on 4/22/18.
 */

class ApiObserver(block: suspend () -> String, dispatcher: CoroutineDispatcher, toast: Boolean = false, responseListener: ResponseListener) {

    constructor(block: suspend () -> String, toast: Boolean = false, responseListener: ResponseListener) : this(block, Dispatchers.IO, toast, responseListener)

    init {
        /*val exception = CoroutineExceptionHandler { coroutineContext, throwable ->
            val response = ApiResponse(isToast = toast).responseError(throwable)
            if (response.isTokenExpired) {
                responseListener.onExpired(response)
            } else {
                responseListener.onError(response)
            }
        }*/

        CoroutineScope(dispatcher).launch {
            try {
                val response = block()
                val responseJson = JSONObject(response)
                responseListener.onSuccess(responseJson)
            } catch (e: Throwable) {
                val response = ApiResponse(isToast = toast).responseError(e)
                if (response.isTokenExpired) {
                    responseListener.onExpired(response)
                } else {
                    responseListener.onError(response)
                }
            }
        }
    }

    interface ResponseListener {
        suspend fun onSuccess(response: JSONObject)

        suspend fun onError(response: ApiResponse) {
            EventBus.getDefault().post(response)
        }

        suspend fun onExpired(response: ApiResponse) {
            EventBus.getDefault().post(response)
        }
    }

    // TODO: response listener suspend version

    companion object {
        /**
         * @param block suspend function
         * @param dispatcher Default [Dispatchers.IO]
         * @param toast Default false
         * @param listener Anonymous class to handle response
         */
        suspend fun apiObserver(block: suspend () -> String, dispatcher: CoroutineDispatcher = Dispatchers.IO, toast: Boolean = false, listener: ResponseListener) {
            try {
                val responseJSON = withContext(dispatcher) {
                    val response = block.invoke()
                    JSONObject(response)
                }
                listener.onSuccess(responseJSON)
            } catch (t: Throwable) {
                val response = ApiResponse(isToast = toast).responseError(t)
                if (response.isTokenExpired) {
                    listener.onExpired(response)
                } else {
                    listener.onError(response)
                }
            }
        }
    }
}