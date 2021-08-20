package com.crocodic.core.aes

import com.crocodic.core.aes.IV.iv
import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by @yzzzd on 4/22/18.
 */

object Crypt {
    @JvmStatic
    fun aesEncrypt(v: String, secretKey: String = iv) = AES256.encrypt(v, secretKey)
    @JvmStatic
    fun aesDecrypt(v: String, secretKey: String = iv) = AES256.decrypt(v, secretKey)
}

private object AES256 {
    private fun cipher(opmode: Int, secretKey: String): Cipher {
        if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))
        c.init(opmode, sk, iv)
        return c
    }

    fun encrypt(str: String, secretKey: String): String {
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        return String(Base64.encodeBase64(encrypted))
    }

    fun decrypt(str: String, secretKey: String): String {
        val byteStr = Base64.decodeBase64(str.toByteArray(Charsets.UTF_8))
        return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
    }
}

private object IV {
    const val iv = "e38402c20d82f9s63ka23b195k30sb83"
}