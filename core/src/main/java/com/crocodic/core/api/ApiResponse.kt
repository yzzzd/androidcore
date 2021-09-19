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

    @Expose
    @SerializedName("status")
    var status: ApiStatus? = ApiStatus.SUCCESS,

    @Expose
    @SerializedName("message")
    var message: String? = null,

    @Expose
    @SerializedName("is_token_expired")
    var isTokenExpired: Boolean = false,

    @Expose
    @SerializedName("throwable")
    var throwableBody: String? = null,

    @Expose
    @SerializedName("flag_view")
    var flagView: Int? = null
) {
    fun responseError(error: Throwable, flag: Int? = null): ApiResponse {
        flagView = flag
        status = ApiStatus.ERROR
        message = error.message

        if (error is HttpException) {
            val errorBody = error.response()?.errorBody()?.string()
            errorBody?.let { eb ->
                try {
                    val responseJson = JSONObject(eb)

                    message = responseJson.getString(ApiCode.MESSAGE)
                    status = when(responseJson.getInt(ApiCode.STATUS)) {
                        400 -> ApiStatus.WRONG
                        401 -> {
                            resetSessionToken()
                            ApiStatus.EXPIRED
                        }
                        else -> ApiStatus.ERROR
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    status = ApiStatus.WRONG
                    message = error.message
                }
            }
        } else if (error is UnknownHostException) {
            status = ApiStatus.WRONG
            message = "Maaf, tidak tersambung dengan server. Silakan coba lagi."
        }

        return this
    }

    fun responseWrong(apiMessage: String, flag: Int? = null): ApiResponse {
        flagView = flag
        status = ApiStatus.WRONG
        message = apiMessage
        return this
    }

    fun responseExpired(apiMessage: String, flag: Int? = null): ApiResponse {
        flagView = flag
        status = ApiStatus.EXPIRED
        message = apiMessage
        resetSessionToken()
        return this
    }

    fun responseSuccess(apiMessage: String, flag: Int? = null): ApiResponse {
        flagView = flag
        status = ApiStatus.SUCCESS
        message = apiMessage
        return this
    }

    fun responseLoading(message: String? = null, flag: Int? = null): ApiResponse {
        this.flagView = flag
        this.status = ApiStatus.LOADING
        this.message = message
        return this
    }

    fun response(code: Int, apiMessage: String, flag: Int? = null): ApiResponse {
        return when(code) {
            ApiCode.SUCCESS -> responseSuccess(apiMessage, flag)
            ApiCode.WRONG -> responseWrong(apiMessage, flag)
            ApiCode.EXPIRED -> responseExpired(apiMessage, flag)
            else -> responseLoading(apiMessage, flag)
        }
    }

    private fun resetSessionToken() {
        isTokenExpired = true
    }

}