package com.crocodic.core.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Created by @yzzzd on 4/22/18.
 */

open class ApiResponse(
    var status: ApiStatus = ApiStatus.SUCCESS,
    var message: String? = null,
    var isTokenExpired: Boolean = false,
    var flagView: Int? = null,
    var isToast: Boolean = false
) {
    fun responseError(error: Throwable? = null, flagView: Int? = null): ApiResponse {
        this.status = ApiStatus.ERROR
        this.flagView = flagView

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

    fun responseExpired(message: String, flagView: Int? = null): ApiResponse {
        this.status = ApiStatus.EXPIRED
        this.message = message
        this.flagView = flagView
        this.isTokenExpired = true
        return this
    }

    fun responseLoading(message: String? = null, flagView: Int? = null): ApiResponse {
        this.status = ApiStatus.LOADING
        this.message = message
        this.flagView = flagView
        return this
    }

    fun responseSuccess(message: String? = null, flagView: Int? = null): ApiResponse {
        this.status = ApiStatus.SUCCESS
        this.message = message
        this.flagView = flagView
        return this
    }
}