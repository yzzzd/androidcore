package com.crocodic.core.helper

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.PixelCopy
import android.view.View
import androidx.annotation.RequiresApi
import androidx.exifinterface.media.ExifInterface
import com.crocodic.core.R
import com.google.gson.GsonBuilder
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Created by @yzzzd on 4/22/18.
 */

object BitmapHelper {

    fun encodeToBase64(image: Bitmap): String {
        var baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var b = baos.toByteArray()
        var temp: String? = null
        try {
            System.gc()
            temp = Base64.encodeToString(b, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            b = baos.toByteArray()
            temp = Base64.encodeToString(b, Base64.DEFAULT)
        }
        return temp ?: "null"
    }

    fun decodeBase64(context: Context, input: String): Bitmap {
        val decodedByte = Base64.decode(input, 0)

        val sdCardDirectory = File(context.cacheDir, "")

        val rand = Random()

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        val randomNum = rand.nextInt((1000 - 0) + 1) + 0

        val nw = "IMG_$randomNum.txt"
        val image = File(sdCardDirectory, nw)

        // Encode the file as a PNG image.
        var outStream: FileOutputStream? = null
        try {
            outStream = FileOutputStream(image)
            outStream.write(input.toByteArray())
            outStream.flush()
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return try {
            BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            val iss = context.resources.openRawResource(R.raw.img_error)
            BitmapFactory.decodeStream(iss)
        } catch (e: Exception) {
            e.printStackTrace()
            val iss = context.resources.openRawResource(R.raw.img_error)
            BitmapFactory.decodeStream(iss)
        }
    }

    fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        if (!img.isRecycled) img.recycle()
        return rotatedImg
    }

    fun rotateBitmapIfRequired(orientation: Int, bitmap: Bitmap): Bitmap {
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 270F)
            ExifInterface.ORIENTATION_ROTATE_180 -> bitmap
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 90F)
            else -> rotateImage(bitmap, 180F)
        }
    }

    fun resizeBitmap(bitmap: Bitmap, maxSize: Float): Bitmap {
        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()

        if (w > h && w < maxSize) {
            return bitmap
        } else if (w < h && h < maxSize) {
            return bitmap
        }

        var nW = w
        var nH = h

        if (w > maxSize || h > maxSize) {
            if (w > h) {
                nW = maxSize
                nH = h / (w / maxSize)
            } else if (w < h) {
                nH = maxSize
                nW = w / (h / maxSize)
            } else {
                nW = w
                nH = h
            }
        }

        val inW = nW.toInt()
        val inH = nH.toInt()

        return Bitmap.createScaledBitmap(bitmap, inW, inH, false)
    }

    fun saveDecodedImage(decodedImage: String, directory: String?): File {
        val path = File(directory)
        //File file = new File(path, DateTimeMasker.generateNameJson(idChecklist));
        val file = File(path, "fototest.txt")
        var outputStream: OutputStream? = null
        val gson =
            GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create()
        try {
            path.mkdirs()
            outputStream = FileOutputStream(file)
            val bufferedWriter: BufferedWriter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                BufferedWriter(
                    OutputStreamWriter(
                        outputStream,
                        StandardCharsets.UTF_8
                    )
                )
            } else {
                BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
            }
            gson.toJson(decodedImage, bufferedWriter)
            bufferedWriter.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return file
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(window, Rect(locationOfViewInWindow[0], locationOfViewInWindow[1], locationOfViewInWindow[0] + view.width, locationOfViewInWindow[1] + view.height), bitmap, { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        callback(bitmap)
                    }
                    // possible to handle other result codes ...
                }, Handler(Looper.getMainLooper()))
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    }

    fun loadBitmapFromView(v: View): Bitmap {
        //Pre-measure the view so that height and width don't remain null.
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        //Assign a size and position to the view and all of its descendants
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)

        val bitmap = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        //Create a canvas with the specified bitmap to draw into
        val c = Canvas(bitmap)

        //Render this view (and all of its children) to the given Canvas
        v.draw(c)
        return bitmap
    }
}