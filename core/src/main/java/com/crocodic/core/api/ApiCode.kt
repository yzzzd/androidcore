package com.crocodic.core.api

/**
 * Created by @yzzzd on 4/22/18.
 */

class ApiCode {
    companion object {
        const val SUCCESS = 200
        const val WRONG = 400
        const val LOADING = 0
        const val EXPIRED = 401
        const val FORBIDDEN = 403

        const val STATUS = "status"
        const val MESSAGE = "message"
        const val DATA = "data"
    }
}