package com.crocodic.core.extension

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Context.drawableRes(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)
fun Context.colorRes(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.uriToFile(uri: Uri, dir: String = Environment.DIRECTORY_PICTURES): File? {
    return try {
        val parcelFileDescriptor = uri.let { contentResolver.openFileDescriptor(it, "r") }
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val inputStream = FileInputStream(fileDescriptor)

        val primaryStorage = ContextCompat.getExternalFilesDirs(this, null)[0]

        val mediaDirectory = File(primaryStorage, dir)
        if (!mediaDirectory.exists()) {
            mediaDirectory.mkdir()
        }

        // val outputFileName = DateTimeHelper().createAt().replace(" ", "_") + "." + getFileExtension(uri)
        val outputFileName = getFileName(uri)
        val outputFile = File(mediaDirectory, outputFileName)
        val outputStream = FileOutputStream(outputFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        outputFile
    } catch (e: Exception) {
        null
    }
}

fun Context.getFileName(uri: Uri): String {
    // File Scheme.
    var fileName = "Untitled"
    if (ContentResolver.SCHEME_FILE == uri.scheme) {
        uri.path?.let {
            val file = File(it)
            fileName = file.name
            // file.length()
        }
    } else if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val returnCursor = contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null && returnCursor.moveToFirst()) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            fileName = returnCursor.getString(nameIndex)
            // val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            // returnCursor.getLong(sizeIndex)
            returnCursor.close()
        }
    }
    return fileName
}

fun Context.getFileExtension(uri: Uri): String {
    return if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(contentResolver.getType(uri)).orEmpty()
    } else {
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path.orEmpty())).toString())
    }
}