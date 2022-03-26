package com.crocodic.core.api

import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

/**
 * Created by @yzzzd on 4/22/18.
 */

open class ApiResponse(
    var status: ApiStatus = ApiStatus.SUCCESS,
    var message: String? = null,
    var isTokenExpired: Boolean = false,
    var flagView: Int? = null,
    var isToast: Boolean = false,
    var data: Any? = null
) {
    fun responseError(error: Throwable? = null, flagView: Int? = null, data: Any? = null): ApiResponse {
        this.status = ApiStatus.ERROR
        this.flagView = flagView
        this.data = data

        if (error is HttpException) {
            error.response()?.errorBody()?.string()?.let { errorBody ->
                message = try {
                    val responseJson = JSONObject(errorBody)
                    responseJson.getString("message")
                } catch (e: JSONException) {
                    e.printStackTrace()
                    e.message
                }
            }
        } else {
            this.message = error?.message
        }

        return this
    }

    fun responseExpired(message: String, flagView: Int? = null, data: Any? = null): ApiResponse {
        this.status = ApiStatus.EXPIRED
        this.message = message
        this.flagView = flagView
        this.isTokenExpired = true
        this.data = data
        return this
    }

    fun responseLoading(message: String? = null, flagView: Int? = null, data: Any? = null): ApiResponse {
        this.status = ApiStatus.LOADING
        this.message = message
        this.flagView = flagView
        this.data = data
        return this
    }

    fun responseSuccess(message: String? = null, flagView: Int? = null, data: Any? = null): ApiResponse {
        this.status = ApiStatus.SUCCESS
        this.message = message
        this.flagView = flagView
        this.data = data
        return this
    }

    inline fun <reified T> dataAs(): T? {
        return try {
            data as T
        } catch (e: ClassCastException) {
            e.printStackTrace()
            null
        }
    }
}