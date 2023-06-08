package com.crocodic.core.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.crocodic.core.helper.okhttp.SSLTrust
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

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

    fun hasActiveInternet(context: Context, testUrl: String = "https://www.google.com/generate_204", connected: (result: Boolean) -> Unit) {
        //https://www.google.com/generate_204
        //https://clients3.google.com/generate_204
        CoroutineScope(Dispatchers.IO).launch {
            if (isNetworkAvailable(context)) {
                try {
                    val urlc = withContext(Dispatchers.IO) {
                        URL(testUrl).openConnection()
                    }.apply {
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

    fun provideOkHttpClient(timeOut: Long = 90, withUnsafeTrustManager: Boolean = true, withInterceptor: Boolean = false): OkHttpClient {

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)

        if (withUnsafeTrustManager) {
            val unsafeTrustManager = SSLTrust().createUnsafeTrustManager()
            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, arrayOf(unsafeTrustManager), null)
            }
            okHttpClient.sslSocketFactory(sslContext.socketFactory,  unsafeTrustManager)
        }

        if (withInterceptor) {
            val interceptors = HttpLoggingInterceptor()
            interceptors.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(interceptors)
        }

        return okHttpClient.build()
    }

    inline fun <reified T> provideApiService(baseUrl: String, okHttpClient: OkHttpClient = provideOkHttpClient(), converterFactory: List<Converter.Factory>? = null): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)

        converterFactory?.forEach {
            retrofit.addConverterFactory(it)
        }

        return retrofit.build().create(T::class.java)
    }

}