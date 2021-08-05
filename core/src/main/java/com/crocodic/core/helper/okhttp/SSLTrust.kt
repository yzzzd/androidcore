package com.crocodic.core.helper.okhttp

import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Arrays
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import okhttp3.CertificatePinner
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer

/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class SSLTrust {

    private val client: OkHttpClient

    @Throws(Exception::class)
    fun run() {
        val request = Request.Builder()
            .url("https://publicobject.com/helloworld.txt")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code " + response)
            val responseHeaders: Headers = response.headers
            for (i in 0 until responseHeaders.size) {
                println(responseHeaders.name(i) + ": " + responseHeaders.value(i))
            }
            println(response.body!!.string())
        }
    }

    /**
     * Returns an input stream containing one or more certificate PEM files. This implementation just
     * embeds the PEM files in Java strings; most applications will instead read this from a resource
     * file that gets bundled with the application.
     */
    fun trustedCertificatesInputStream(): InputStream {
        // PEM files for root certificates of Comodo and Entrust. These two CAs are sufficient to view
        // https://publicobject.com (Comodo) and https://squareup.com (Entrust). But they aren't
        // sufficient to connect to most HTTPS sites including https://godaddy.com and https://visa.com.
        // Typically developers will need to get a PEM file from their organization's TLS administrator.
        val entrustRootCertificateAuthority = (""
                + "-----BEGIN CERTIFICATE-----\n" +
                "MIIF7DCCBNSgAwIBAgIQZj0UDclHsCHCzk79Pn2dwTANBgkqhkiG9w0BAQsFADCB\n" +
                "jzELMAkGA1UEBhMCR0IxGzAZBgNVBAgTEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                "A1UEBxMHU2FsZm9yZDEYMBYGA1UEChMPU2VjdGlnbyBMaW1pdGVkMTcwNQYDVQQD\n" +
                "Ey5TZWN0aWdvIFJTQSBEb21haW4gVmFsaWRhdGlvbiBTZWN1cmUgU2VydmVyIENB\n" +
                "MB4XDTE5MDEyMjAwMDAwMFoXDTIwMDEyMjIzNTk1OVowTjEhMB8GA1UECxMYRG9t\n" +
                "YWluIENvbnRyb2wgVmFsaWRhdGVkMRQwEgYDVQQLEwtQb3NpdGl2ZVNTTDETMBEG\n" +
                "A1UEAxMKcmVwcmltZS5pZDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\n" +
                "ANhCKiXyPWzwDoK6jBdT+WhHKvkvTNCNS69P8MS0sBdw+3fx4V35sw7XXgcwSg5R\n" +
                "9HIWrENj8aqSLr2P1mwnbyAma+mDwLq2ZSPci8l57J0K3qkYm+QwpNT5AAop3+KR\n" +
                "nhLff00djzik2logXbve5EUIx9Wm6KYejn6BXZ2Dyou1Y35AbuFAZRH8L1o0dakq\n" +
                "L6bm38N8RPjeMiwQ9ak6MZucnaCVm5K1i8juzXx+k1k5oP3VS7dT7xptkCzepQ0N\n" +
                "qyeMKNcp06VIUSMkpGuq+U/OuKtqWYR9w1GsN16NjYov8TFClSVhcCoyb9z+sm/r\n" +
                "eBImh5zpaFFRWmQJhE3qYkUCAwEAAaOCAoIwggJ+MB8GA1UdIwQYMBaAFI2MXsRU\n" +
                "rYrhd+mb+ZsF4bgBjWHhMB0GA1UdDgQWBBShh8IIkyKtiBCQ3zt9JkSJ//bo0jAO\n" +
                "BgNVHQ8BAf8EBAMCBaAwDAYDVR0TAQH/BAIwADAdBgNVHSUEFjAUBggrBgEFBQcD\n" +
                "AQYIKwYBBQUHAwIwSQYDVR0gBEIwQDA0BgsrBgEEAbIxAQICBzAlMCMGCCsGAQUF\n" +
                "BwIBFhdodHRwczovL3NlY3RpZ28uY29tL0NQUzAIBgZngQwBAgEwgYQGCCsGAQUF\n" +
                "BwEBBHgwdjBPBggrBgEFBQcwAoZDaHR0cDovL2NydC5zZWN0aWdvLmNvbS9TZWN0\n" +
                "aWdvUlNBRG9tYWluVmFsaWRhdGlvblNlY3VyZVNlcnZlckNBLmNydDAjBggrBgEF\n" +
                "BQcwAYYXaHR0cDovL29jc3Auc2VjdGlnby5jb20wJQYDVR0RBB4wHIIKcmVwcmlt\n" +
                "ZS5pZIIOd3d3LnJlcHJpbWUuaWQwggEEBgorBgEEAdZ5AgQCBIH1BIHyAPAAdgC7\n" +
                "2d+8H4pxtZOUI5eqkntHOFeVCqtS6BqQlmQ2jh7RhQAAAWh1TAT8AAAEAwBHMEUC\n" +
                "IHAvE1Mnwqr/RxVTlbe8fi09eI+sYeYwj6RM4MVyLDb0AiEA1hRydBp/G20DjTHp\n" +
                "/65Q3bhK/CsDZ9LNOG8ne9IrMqkAdgBep3P531bA57U2SH3QSeAyepGaDIShEhKE\n" +
                "GHWWgXFFWAAAAWh1TAVCAAAEAwBHMEUCIBz7KtS+BOfYBbDnEzn3DCHjanSW6Np0\n" +
                "11G0DppVArrsAiEA5dJVC5VL6ryEGu2JiHSdTQdh+dBa1z/GIdlYOJEMGWswDQYJ\n" +
                "KoZIhvcNAQELBQADggEBAHYJjmfjJ3VrFC1HlOBezDXH/DXfO27qEDK9ua6bIVEd\n" +
                "qlMiUnhBydfsDGw00T2pv7YVUmf/QIH0yUSknkHMYVAewMNIrQHXUD5DxrKS+43U\n" +
                "GvDtzbUwoLy2cW7i45UNnb6H7ifJMubOizbV3hchNGTYEpBjp6a59x3ISdmKGpe2\n" +
                "7MBzvuNuKd+gW1TVEMJCfWDAcKcCjy646zLOWOFlWuFj1A9vhUxECDe9cmVjkBPN\n" +
                "yQz4gh9EWJQkKYMxVFXyKhJBGPLrxa1x0rRd188k1hxWpcf4KqVR3ejCYlF/ts3Z\n" +
                "Ul/pQz/tOYX5llkeoHR0jbGUFrac1NXqiyTghKTPIcs=\n" +
                "-----END CERTIFICATE-----")
        return Buffer()
            .writeUtf8(entrustRootCertificateAuthority)
            .inputStream()
    }

    /**
     * Returns a trust manager that trusts `certificates` and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a `SSLHandshakeException`.
     *
     *
     * This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     *
     * See also [CertificatePinner], which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     *
     * Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    @Throws(GeneralSecurityException::class)
    fun trustManagerForCertificates(`in`: InputStream?): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificates = certificateFactory.generateCertificates(`in`)
        if (certificates.isEmpty()) {
            throw IllegalArgumentException("expected non-empty set of trusted certificates")
        }

        // Put the certificates a key store.
        val password = "password".toCharArray() // Any password will work.
        val keyStore = newEmptyKeyStore(password)
        for ((index, certificate: Certificate?) in certificates.withIndex()) {
            val certificateAlias = index.toString()
            keyStore.setCertificateEntry(certificateAlias, certificate)
        }

        // Use it to build an X509 trust manager.
        val keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        )
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException(
                "Unexpected default trust managers:"
                        + Arrays.toString(trustManagers)
            )
        }
        return trustManagers[0] as X509TrustManager
    }

    @Throws(GeneralSecurityException::class)
    private fun newEmptyKeyStore(password: CharArray): KeyStore {
        try {
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val `in`: InputStream? = null // By convention, 'null' creates an empty key store.
            keyStore.load(`in`, password)
            return keyStore
        } catch (e: IOException) {
            throw AssertionError(e)
        }
    }

    fun createUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            SSLTrust().run()
        }
    }

    init {
        val trustManager: X509TrustManager
        val sslSocketFactory: SSLSocketFactory
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream())
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
            sslSocketFactory = sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
        client = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .build()
    }
}