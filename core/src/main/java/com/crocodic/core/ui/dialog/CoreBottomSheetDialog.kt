package com.crocodic.core.ui.dialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import com.crocodic.core.R
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by @yzzzd on 4/22/18.
 */

abstract class CoreBottomSheetDialog<VB : ViewBinding>(val context: Context) {

    protected var binding: VB
    protected var dialog: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialog)

    protected abstract fun getViewBinding(): VB

    init {
        binding = this.getViewBinding()
        dialog.setContentView(binding.root)
    }

    fun onDismiss(onDismiss: () -> Unit): CoreBottomSheetDialog<VB> {
        dialog.setOnDismissListener {
            onDismiss()
        }
        return this
    }

    protected fun string(@StringRes res: Int): String {
        return context.getString(res)
    }

    open fun show(): CoreBottomSheetDialog<VB> {
        dialog.show()
        return this
    }

    fun setCancelable(cancelable: Boolean): CoreBottomSheetDialog<VB> {
        dialog.setCancelable(cancelable)
        return this
    }

    fun isShowing() = dialog.isShowing

    open fun dismiss(): CoreBottomSheetDialog<VB> {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
        return this
    }
}
