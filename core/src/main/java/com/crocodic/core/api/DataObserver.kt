package com.crocodic.core.api

/**
 * Created by @yzzzd on 4/22/18.
 */

class DataObserver<T>(val page: Int, val datas: List<T?>, val cache: Boolean = false)