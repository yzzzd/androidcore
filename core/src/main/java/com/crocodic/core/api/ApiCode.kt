package com.crocodic.core.api

/**
 * Created by @yzzzd on 4/22/18.
 */

class ApiCode {
    companion object {
        const val SUCCESS = 200
        const val WRONG = 400
        const val EXPIRED = 401
        const val ERROR = 500

        const val STATUS = "status"
        const val MESSAGE = "message"
        const val DATA = "data"
        const val ERROR_MESSAGE = "error"

        const val NOT_FOUND = "not_found" //url tidak ada
        const val BAD_REQUEST = "bad_request" //request tidak sesuai
        const val INVALID_TOKEN = "invalid_token" //token salah
        const val EXPIRED_TOKEN = "expired_token" //token expired
        const val UNAUTHORIZED = "unauthorized" //token tidak dikirim
        const val FORBIDDEN = "forbidden" //forbidden
        const val INVALID_LOGIN = "invalid_login" //credential login tidak sesuai
    }
}