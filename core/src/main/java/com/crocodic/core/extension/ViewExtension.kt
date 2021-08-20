package com.crocodic.core.extension

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.R
import com.crocodic.core.aes.Crypt
import com.crocodic.core.helper.ImagePreviewHelper
import com.crocodic.core.helper.StringHelper
import com.crocodic.core.helper.list.EndlessScrollListener
import com.crocodic.core.helper.util.Dpx
import com.google.android.material.snackbar.Snackbar

/**
 * Created by @yzzzd on 4/22/18.
 */

fun String.encrypt(secret: String? = null) = if (secret.isNullOrEmpty()) Crypt.aesEncrypt(this) else Crypt.aesEncrypt(this, secret)

fun String.decrypt(secret: String? = null) = if (secret.isNullOrEmpty()) Crypt.aesDecrypt(this) else Crypt.aesDecrypt(this, secret)

fun String.isUrl() = this.contains("http")

fun List<EditText?>.isEmptyRequired(@StringRes message: Int): Boolean {
    for (e in this) {
        if (e?.textOf().isNullOrEmpty()) {
            e?.error = e?.context?.getString(message)
            e?.requestFocus()
            return true
        }
    }
    return false
}

fun EditText.isEmptyRequired(@StringRes message: Int): Boolean {
    if (this.textOf().isEmpty()) {
        this.error = this.context?.getString(message)
        this.requestFocus()
        return true
    }
    return false
}

fun TextView.text(value: String?) {
    this.text = StringHelper.validateEmpty(value)
}

fun TextView.textOf() = this.text.toString().trim()

fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG, @StringRes action: Int? = null, listener: View.OnClickListener? = null) {
    Snackbar.make(this, message, duration).apply {
        this.view.setPadding(Dpx.dpToPx(24), Dpx.dpToPx(24), Dpx.dpToPx(24), Dpx.dpToPx(32))
        this.view.background = AppCompatResources.getDrawable(this.context, R.drawable.background_snackbar)
        ViewCompat.setElevation(this.view, 6f)

        if (action != null && listener != null) {
            setAction(action, listener)
            setActionTextColor(Color.WHITE)
        }
    }.show()
}

fun View.snacked(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
    this.snack(this.context.getString(message), duration)
}

fun View.snacked(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    this.snack(message, duration)
}

fun View.snacked(@StringRes message: Int, @StringRes action: Int, listener: View.OnClickListener) {
    this.snack(this.context.getString(message), Snackbar.LENGTH_LONG, action, listener)
}

/* To hide soft keybard */
fun View.hideSoftKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
    this.clearFocus()
}

/* Setup loadmore listener */
fun RecyclerView.initLoadMore(onLoad: (page: Int) -> Unit): EndlessScrollListener {
    val el = object : EndlessScrollListener(this.layoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            // Triggered only when new data needs to be appended to the list
            view?.post { onLoad(page) }
        }
    }
    addOnScrollListener(el)
    return el
}

/* preview the image */
fun View.preview(image: String?) {
    if (this is ImageView) {
        ImagePreviewHelper(this.context).show(this, image)
    }
}

/* preview the array of image */
fun View.preview(images: List<String?>?, position: Int = 0) {
    if (this is ImageView) {
        ImagePreviewHelper(this.context).show(this, images, position)
    }
}