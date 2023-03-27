package com.crocodic.core.api

import com.google.gson.Gson

open class ModelResponse(
    var status: ApiStatus = ApiStatus.SUCCESS,
    var message: String? = null,
    var flagView: Int? = null,
    var isToast: Boolean = false
) {
    open fun success(message: String? = null): ModelResponse {
        this.status = ApiStatus.SUCCESS
        updateMessage(message)
        return this
    }

    open fun error(message: String? = null): ModelResponse {
        this.status = ApiStatus.ERROR
        updateMessage(message)
        return this
    }

    open fun expired(message: String? = null): ModelResponse {
        this.status = ApiStatus.EXPIRED
        updateMessage(message)
        return this
    }

    open fun loading(): ModelResponse {
        this.status = ApiStatus.LOADING
        return this
    }

    private fun updateMessage(message: String?) {
        message?.let { this.message = it }
    }

    companion object {
        @Throws(Exception::class)
        inline fun <reified T> create(status: ApiStatus): T {
            return try {
                val gson = Gson()
                val response = when (status) {
                    ApiStatus.LOADING -> ModelResponse().loading()
                    ApiStatus.SUCCESS -> ModelResponse().success()
                    ApiStatus.ERROR -> ModelResponse().error()
                    ApiStatus.EXPIRED -> ModelResponse().expired()
                }
                val strResponse = gson.toJson(response)
                gson.fromJson(strResponse, T::class.java)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}