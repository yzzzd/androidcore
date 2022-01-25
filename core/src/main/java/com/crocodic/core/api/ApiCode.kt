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
    }
}