package com.crocodic.core.extension

import android.content.Context
import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import java.io.File

/**
 * Created by @yzzzd on 4/22/18.
 */

suspend fun File.compress(context: Context, prefWidth: Int, prefHeight: Int, prefSize: Long): File {
    return Compressor.compress(context, this) {
        resolution(prefWidth, prefHeight)
        quality(100)
        format(Bitmap.CompressFormat.WEBP_LOSSLESS)
        size(prefSize)
    }
}