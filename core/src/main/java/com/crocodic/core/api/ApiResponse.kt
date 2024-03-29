package com.crocodic.core.api

import com.crocodic.core.extension.extractInt
import com.crocodic.core.extension.extractString
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
    var data: Any? = null,
    var rawResponse: String? = null
) {
    fun responseError(error: Throwable? = null, flagView: Int? = null, data: Any? = null): ApiResponse {
        this.status = ApiStatus.ERROR
        this.flagView = flagView
        this.data = data

        if (error is HttpException) {
            rawResponse = error.response()?.errorBody()?.string()?.trim()
            rawResponse?.let { errorBody ->
                message = try {
                    val responseJson = JSONObject(errorBody)

                    val status = responseJson.extractInt(ApiCode.STATUS)

                    if (status == ApiCode.EXPIRED) {
                        val errorMessage = responseJson.extractString(ApiCode.ERROR_MESSAGE)
                        this.isTokenExpired = errorMessage?.contains(ApiCode.EXPIRED_TOKEN) == true
                    }

                    responseJson.extractString(ApiCode.MESSAGE)
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
            data as? T
        } catch (e: ClassCastException) {
            e.printStackTrace()
            null
        }
    }
}