package com.crocodic.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Created by @yzzzd on 4/22/18.
 */

open class CoreSession(context: Context) {

    companion object {
        const val PREF_NAME = "_core_"
        const val PREF_FCMID = "fcm_id"
        const val PREF_UID = "user_id"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    //private var pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var pref: SharedPreferences = EncryptedSharedPreferences.create(context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setValue(key: String, value: String) {
        val editor = pref.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun setValue(key: String, value: Boolean) {
        val editor = pref.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun setValue(key: String, value: Int) {
        val editor = pref.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun setValue(key: String, value: Long) {
        val editor = pref.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }

    fun setValue(key: String, value: Float) {
        val editor = pref.edit()
        editor?.putFloat(key, value)
        editor?.apply()
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun getString(key: String): String {
        return pref.getString(key, "") ?: ""
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun getLong(key: String): Long {
        return pref.getLong(key, 0L)
    }

    fun getFloat(key: String): Float {
        return pref.getFloat(key, 0f)
    }

    fun clearAll() {
        val bkFcmId = getString(PREF_FCMID)
        val bkUserId = getString(PREF_UID)

        val editor = pref.edit()
        editor.clear()
        editor.apply()

        setValue(PREF_FCMID, bkFcmId)
        setValue(PREF_UID, bkUserId)
    }
}