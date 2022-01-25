package com.crocodic.core.extension

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by @yzzzd on 4/22/18.
 */

inline fun <reified T> JSONObject.toObject(gson: Gson): T {
    return gson.fromJson(this.toString(), T::class.java)
}

inline fun <reified T> String.toObject(gson: Gson): T {
    return gson.fromJson(this, T::class.java)
}

inline fun <reified T> T.toJson(gson: Gson): String {
    return gson.toJson(this)
}

inline fun <reified T> JSONArray.toList(gson: Gson): List<T> {
    return gson.fromJson(this.toString(), object : TypeToken<ArrayList<T>?>() {}.type)
}

inline fun <reified T> String.toList(gson: Gson): List<T> {
    return gson.fromJson(this, object : TypeToken<ArrayList<T>?>() {}.type)
}
