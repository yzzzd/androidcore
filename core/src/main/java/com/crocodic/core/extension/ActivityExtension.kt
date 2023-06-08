package com.crocodic.core.extension

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import androidx.core.view.WindowCompat
import com.crocodic.core.R
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.data.Const
import com.crocodic.core.databinding.CrSnackbarBinding
import com.crocodic.core.data.model.AppNotification
import com.crocodic.core.ui.permission.CameraPermissionActivity
import com.crocodic.core.ui.permission.LocationPermissionActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.tapadoo.alerter.Alerter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by @yzzzd on 4/22/18.
 */

@Deprecated("Next toast cannot be custom view", ReplaceWith("tos(message)"))
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

@Deprecated("Next toast cannot be custom view", ReplaceWith("tos(message)"))
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

fun Context.tos(message: Int, short: Boolean = false) {
    tos(getString(message), short)
}

fun Context.tos(message: String, short: Boolean = false) {
    Toast.makeText(this, message, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
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
@Deprecated("Use PermissionHelper.hasPermission instead")
fun Context.allPermissionsGranted(permission: Array<String>): Boolean {
    permission.forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

/* To check camera permission */
fun NoViewModelActivity<*>.checkCameraPermission(exit: Boolean, onComplete: () -> Unit) {
    val REQUIRED_PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    if (allPermissionsGranted(REQUIRED_PERMISSIONS_CAMERA)) {
        onComplete()
    } else {
        activityLauncher.launch(createIntent<CameraPermissionActivity>()) {
            if (allPermissionsGranted(REQUIRED_PERMISSIONS_CAMERA)) {
                onComplete()
            } else {
                tos(R.string.cr_error_permission_denied)
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
                tos(R.string.cr_error_permission_denied)
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
fun Context.openMap() {
    try {
        packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")?.let {
            startActivity(it)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/* Open the dial activity */
fun Context.openDial(phone: String) {
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone}")))
}

/* Pop in app notification from the top */
fun Activity.notify(appNotification: AppNotification, sound: Uri? = null, titleFont: Typeface? = null, contentFont: Typeface? = null, onClick: () -> Unit) {

    playSound(sound)
    playVibrate(80)

    Alerter.create(this, R.layout.cr_bubble_notification)
        .setBackgroundColorRes(android.R.color.transparent)
        .enableSwipeToDismiss()
        .enableClickAnimation(false)
        .enableVibration(false)
        .setOnClickListener {
            Alerter.hide()
            onClick()
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

fun NoViewModelActivity.BetterActivityResult<Intent, ActivityResult>.openGallery(context: Context, result: (File?, Exception?) -> Unit) {
    val galleyIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

    launch(galleyIntent) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imgUri = it.data?.data

            if (imgUri != null) {
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(imgUri, "r")
                val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                val inputStream = FileInputStream(fileDescriptor)

                // val fileName = "JPEG_FRIEND_.jpg"
                // val primaryStorage = ContextCompat.getExternalFilesDirs(this@ProfileActivity, null)[0]
                // val outputFile = File(primaryStorage, fileName)
                val outputFile = context.createImageFile()
                val outputStream = FileOutputStream(outputFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                parcelFileDescriptor?.close()

                result(outputFile, null)
            } else {
                result(null, Exception())
            }

        } else {
            result(null, Exception())
        }
    }
}

fun NoViewModelActivity.BetterActivityResult<Intent, ActivityResult>.openCamera(context: Context, authority: String = "${context.packageName}.${Const.ACTIVITY.FILE_PROVIDER}", result: (File?, Exception?) -> Unit) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    val photoFile = try {
        context.createImageFile()
    } catch (e: IOException) {
        // Error occurred while creating the File
        result(null, e)
        null
    } ?: return

    val photoURI = FileProvider.getUriForFile(context, authority, photoFile)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

    try {
        launch(cameraIntent) {
            if (it.resultCode == Activity.RESULT_OK) {
                result(photoFile, null)
            } else {
                result(null, Exception())
            }
        }
    } catch (e: ActivityNotFoundException) {
        result(null, e)
        e.printStackTrace()
    }
}

@Throws(IOException::class)
fun Context.createImageFile(prefix: String = "JPEG_", suffix: String = ".jpg"): File {
    // Create an image file name
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(prefix, suffix, storageDir)
}