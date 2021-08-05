package com.crocodic.core.helper.okhttp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@GlideModule
class AppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        unsafeOkHttpClient().let {
            registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(it))
            registry.prepend(InputStream::class.java, Bitmap::class.java, PageDecoder(glide.bitmapPool))
        }
    }

    fun unsafeOkHttpClient(): OkHttpClient {
        val unsafeTrustManager = createUnsafeTrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(unsafeTrustManager), null)
        return OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS)
            .sslSocketFactory(sslContext.socketFactory,  unsafeTrustManager)
            .hostnameVerifier { hostName, sslSession -> true }
            .build()
    }

    fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<out X509Certificate>? {
                return emptyArray()
            }
        }
    }

    class PageDecoder(private val bitmapPool: BitmapPool) : ResourceDecoder<InputStream, Bitmap> {

        companion object {
            val PAGE_DECODER: Option<Boolean> = Option.memory("abc")
        }

        override fun decode(source: InputStream, width: Int, height: Int, options: Options): Resource<Bitmap>? {
            return BitmapResource.obtain(BitmapFactory.decodeStream(source), bitmapPool)
        }

        override fun handles(source: InputStream, options: Options): Boolean = options.get(PAGE_DECODER) ?: false

    }
}