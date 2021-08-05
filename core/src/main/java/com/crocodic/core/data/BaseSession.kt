package com.crocodic.core.data

import android.content.Context
import android.content.SharedPreferences
import com.crocodic.core.extension.decrypt
import com.crocodic.core.extension.encrypt

/**
 * Created by nuryazid on 4/21/18.
 */

open class BaseSession(context: Context) {

    var PREF_NAME = "_core_"
    var PREF_FCMID = "fcm_id".encrypt()
    var PREF_UID = "user_id".encrypt()

    private var pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setValue(key: String, value: String?) {
        val editor = pref.edit()
        if (value.isNullOrEmpty()) {
            editor?.putString(key, value)
        } else {
            editor?.putString(key, value.encrypt())
        }
        editor?.apply()
    }

    fun setValue(key: String, value: Boolean = true) {
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

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun getString(key: String): String {
        val valueSrc = pref.getString(key, "") ?: ""

        return if (valueSrc.isEmpty()) {
            valueSrc
        } else {
            valueSrc.decrypt()
        }
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
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