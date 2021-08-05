package com.crocodic.core.extension

import android.app.Activity
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.pm.ShortcutManagerCompat
import com.crocodic.core.model.AppNotification
import org.greenrobot.eventbus.EventBus

/* clear all pendign notification which not yet opened */
fun Activity.clearNotification() {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()
    ShortcutManagerCompat.removeAllDynamicShortcuts(this)
}

/* play notification sound */
fun Activity.playSound(uriSound: Uri?) {
    try {
        val uri = uriSound?:Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/beep")
        val r = RingtoneManager.getRingtone(this, uri)
        r.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/* bzzz make some vibrate */
fun Activity.playVibrate(ms: Long) {
    val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        //deprecated in API 26
        v.vibrate(ms)
    }
    //window.decorView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
}

fun popNotification(title: String?, content: String?) {
    EventBus.getDefault().post(AppNotification(title = title, content = content))
}