package com.crocodic.core.extension

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.crocodic.core.R
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.databinding.CrSnackbarBinding
import com.crocodic.core.model.AppNotification
import com.crocodic.core.ui.permission.CameraPermissionActivity
import com.crocodic.core.ui.permission.LocationPermissionActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.tapadoo.alerter.Alerter

fun Context.pop(message: Int) {
    val binding = CrSnackbarBinding.inflate(LayoutInflater.from(this))
    binding.message.setText(message)

    Toast(this).apply {
        setGravity(Gravity.CENTER or Gravity.BOTTOM, 0, 0)
        duration = Toast.LENGTH_LONG
        view = binding.root
    }.show()

    //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.pop(message: String) {
    val binding = CrSnackbarBinding.inflate(LayoutInflater.from(this))
    binding.message.text = message

    Toast(this).apply {
        setGravity(Gravity.CENTER or Gravity.BOTTOM, 0, 0)
        duration = Toast.LENGTH_LONG
        view = binding.root
    }.show()

    //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

inline fun <reified T : Activity> Context.openActivity(block: Intent.() -> Unit = {}) {
    startActivity(createIntent<T>(block))
}

inline fun <reified T : Activity> Context.openActivity(vararg pair: Pair<View, String>, block: Intent.() -> Unit = {}) {
    val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this as Activity, *pair).toBundle()
    startActivity(createIntent<T>(block), bundle)
}

inline fun <reified T : Activity> Context.createIntent(block: Intent.() -> Unit = {}): Intent {
    return Intent(this, T::class.java).apply(block)
}

/* make UI become fullscreen */
fun AppCompatActivity.setFullScreen(isStable: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (!isStable) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    } else {
        if (isStable) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    /*WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, mainContainer).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }*/
}

/* check some permission has been granted */
fun Activity.allPermissionsGranted(permission: Array<String>): Boolean {
    permission.forEach {
        if (ContextCompat.checkSelfPermission(baseContext, it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

/* To check camera permission */
fun NoViewModelActivity<*>.checkCameraPermission(exit: Boolean, file: Boolean = false, onComplete: () -> Unit) {
    val REQUIRED_PERMISSIONS_CAMERA_FILE = arrayOf(Manifest.permission.CAMERA)
    val REQUIRED_PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)

    if (allPermissionsGranted(if (file) REQUIRED_PERMISSIONS_CAMERA_FILE else REQUIRED_PERMISSIONS_CAMERA)) {
        onComplete()
    } else {
        activityLauncher.launch(createIntent<CameraPermissionActivity>{
            putExtra(CameraPermissionActivity.CONTENT, file)
        }) {
            if (allPermissionsGranted(if (file) REQUIRED_PERMISSIONS_CAMERA_FILE else REQUIRED_PERMISSIONS_CAMERA)) {
                onComplete()
            } else {
                pop(R.string.cr_error_permission_denied)
                if (exit) {
                    finish()
                }
            }
        }
    }
}

/* To check location permission then check location is enabled */
fun NoViewModelActivity<*>.checkLocationPermission(onComplete: () -> Unit) {
    val REQUIRED_PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    if (allPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION)) {
        onComplete()
    } else {
        activityLauncher.launch(createIntent<LocationPermissionActivity>()) {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS_LOCATION)) {
                onComplete()
            } else {
                pop(R.string.cr_error_permission_denied)
                finish()
            }
        }
    }
}

/* Check location service is enable? */
fun Activity.checkEnabledLocation(launcher: ActivityResultLauncher<IntentSenderRequest>, onComplete: () -> Unit) {

    val locationRequest = LocationRequest.create()
    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)

    val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

    result.addOnCompleteListener {
        try {
            val response = result.getResult(ApiException::class.java)
            // All location settings are satisfied. The client can initialize location requests here.
            onComplete()
        } catch (e: ApiException) {
            when (e.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                    try {
                        // Cast to a resolvable exception.
                        // val resolvable = e as ResolvableApiException
                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                        // resolvable.startResolutionForResult(this, 613)

                        val intentSenderRequest = IntentSenderRequest.Builder((e as ResolvableApiException).resolution).build()
                        launcher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    } catch (e: ClassCastException) {
                        // Ignore, should be an impossible error.
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> onComplete()
            }
        }
    }
}

/* open the google maps app */
fun Activity.openMap() {
    try {
        packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")?.let {
            startActivity(it)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/* Pop in app notification from the top */
fun Activity.notify(appNotification: AppNotification, classObject: Class<*>?, sound: Uri? = null, titleFont: Typeface? = null, contentFont: Typeface? = null) {

    playSound(sound)
    playVibrate(80)

    Alerter.create(this, R.layout.cr_bubble_notification)
        .setBackgroundColorRes(android.R.color.transparent)
        .enableSwipeToDismiss()
        .enableClickAnimation(false)
        .enableVibration(false)
        .setOnClickListener {
            Alerter.hide()
            classObject?.let { co -> startActivity(Intent(this, co)) }
        }
        .also {
            it.getLayoutContainer()?.findViewById<TextView>(R.id.title)?.apply {
                text = appNotification.title
                titleFont?.let { f -> typeface = f }
            }
            it.getLayoutContainer()?.findViewById<TextView>(R.id.content)?.apply {
                text = appNotification.content
                contentFont?.let { f -> typeface = f }
            }
        }
        .show()
}