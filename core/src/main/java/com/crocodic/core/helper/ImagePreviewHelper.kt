package com.crocodic.core.helper

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.R
import com.crocodic.core.extension.isUrl
import com.stfalcon.imageviewer.StfalconImageViewer
import java.io.File

class ImagePreviewHelper(private val context: Context) {
    fun show(imageView: ImageView, image: String?) {
        if (image.isNullOrEmpty()) return
        StfalconImageViewer.Builder(context, arrayOf(image)) { view, url ->
            //val requestOption = RequestOptions().placeholder(R.drawable.placeholder_img)
            Glide.with(view.context)
                .load(if (url.isUrl()) url else File(url))
                //.apply(requestOption)
                //.transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }.withTransitionFrom(imageView).show(true)
    }

    fun show(imageView: ImageView, images: List<String?>?, position: Int = 0) {
        if (images.isNullOrEmpty()) return
        StfalconImageViewer.Builder(context, images) { view, url ->
            //val requestOption = RequestOptions().placeholder(R.drawable.placeholder_img)
            Glide.with(view.context)
                .load(if (url?.isUrl() == true) url else File(url))
                //.apply(requestOption)
                //.transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
        }.withTransitionFrom(imageView).show(true).setCurrentPosition(position)
    }
}