package com.crocodic.core.api

import com.crocodic.core.extension.extractInt
import com.crocodic.core.extension.extractString
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import retrofit2.HttpException

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

    interface ModelResponseListener<T> {
        suspend fun onLoading(response: T) { }

        suspend fun onSuccess(response: T) { }

        suspend fun onError(response: T) {
            EventBus.getDefault().post(response)
        }

        suspend fun onExpired(response: T) {
            EventBus.getDefault().post(response)
        }
    }

    open class ResponseListenerFlow<T>(private val dataFlow: MutableSharedFlow<T>): ModelResponseListener<T> {
        override suspend fun onLoading(response: T) {
            dataFlow.emit(response)
            super.onLoading(response)
        }

        override suspend fun onSuccess(response: T) {
            dataFlow.emit(response)
            super.onSuccess(response)
        }

        override suspend fun onError(response: T) {
            dataFlow.emit(response)
            super.onError(response)
        }

        override suspend fun onExpired(response: T) {
            dataFlow.emit(response)
            super.onExpired(response)
        }
    }

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

        /**
         * @param block suspend function
         * @param toast Default false
         * @param listener Anonymous class to handle response
         */
        suspend inline fun <reified T> run(block: () -> T, toast: Boolean = false, listener: ModelResponseListener<T>) {
            try {
                listener.onLoading(ModelResponse.create(ApiStatus.LOADING))
                val response = block.invoke()
                (response as ModelResponse).success().apply {
                    isToast = toast
                }
                listener.onSuccess(response)
            } catch (e: Exception) {
                if (e is HttpException) {
                    e.response()?.errorBody()?.string()?.let { errorBody ->
                        val response = Gson().fromJson(errorBody, T::class.java)

                        val jsonError = JSONObject(errorBody)
                        val status = jsonError.extractInt(ApiCode.STATUS)
                        val errorMessage = jsonError.extractString(ApiCode.ERROR_MESSAGE)

                        (response as ModelResponse).error(errorMessage).apply {
                            isToast = toast
                        }

                        if (status == ApiCode.EXPIRED && errorMessage?.contains(ApiCode.EXPIRED_TOKEN) == true) {
                            (response as ModelResponse).expired()
                            listener.onExpired(response)
                        } else {
                            listener.onError(response)
                        }
                    }
                } else {
                    val response = ModelResponse.create<T>(ApiStatus.ERROR)
                    (response as ModelResponse).apply {
                        message = e.message
                        isToast = toast
                    }
                    listener.onError(response)
                }
            }
        }
    }
}