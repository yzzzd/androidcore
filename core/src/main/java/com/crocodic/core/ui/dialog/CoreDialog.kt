package com.crocodic.core.ui.dialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import com.crocodic.core.R
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by nuryazid on 4/20/18.
 */

abstract class CoreDialog<VB : ViewBinding>(val context: Context) {

    protected var binding: VB
    protected var dialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)

    protected abstract fun getViewBinding(): VB

    init {
        binding = this.getViewBinding()
        dialog.setContentView(binding.root)
    }

    fun onDismiss(onDismiss: () -> Unit): CoreDialog<VB> {
        dialog.setOnDismissListener {
            onDismiss()
        }
        return this
    }

    protected fun string(@StringRes res: Int): String {
        return context.getString(res)
    }

    open fun show(): CoreDialog<VB> {
        dialog.show()
        return this
    }

    fun setCancelable(cancelable: Boolean): CoreDialog<VB> {
        dialog.setCancelable(cancelable)
        return this
    }

    fun isShowing() = dialog.isShowing

    open fun dismiss(): CoreDialog<VB> {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        return this
    }
}
