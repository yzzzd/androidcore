package com.crocodic.core.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by @yzzzd on 4/22/18.
 */

object NetworkHelper {
    fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.getNetworkCapabilities(cm.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            cm.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
        } ?: false
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.getNetworkCapabilities(cm.activeNetwork) != null
        } else {
            cm.activeNetworkInfo != null
        }
    }

    fun hasActiveInternet(context: Context, connected: (result: Boolean) -> Unit) {
        //https://www.google.com/generate_204
        //https://clients3.google.com/generate_204
        CoroutineScope(Dispatchers.IO).launch {
            if (isNetworkAvailable(context)) {
                try {
                    val urlc = URL("https://www.google.com/generate_204").openConnection().apply {
                        setRequestProperty("User-Agent", "Test")
                        setRequestProperty("Connection", "close")
                        connectTimeout = 1500
                        connect()
                    } as HttpURLConnection
                    connected(urlc.responseCode == 204 && urlc.contentLength == 0)
                } catch (e: IOException) {
                    connected(false)
                }
            } else {
                connected(false)
            }
        }
    }
}