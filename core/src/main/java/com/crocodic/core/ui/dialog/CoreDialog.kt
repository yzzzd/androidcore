package com.crocodic.core.ui.dialog

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * Gunakan [CoreBottomSheetDialog] untuk membuat bottom sheet.
 *
 * @param context
 * @param lifecycleOwner jika di dalam activity gunakan `this` keyword dan
 *                        jika di dalam fragment bisa menggunakan `this`, `requireActivity`, dan `viewLifecycleOwner` (recommended)
 */
abstract class CoreDialog<VB : ViewBinding>(val context: Context, lifecycleOwner: LifecycleOwner) {

    protected var binding: VB
    protected var dialog: androidx.appcompat.app.AlertDialog

    protected abstract fun getViewBinding(): VB

    init {
        binding = this.getViewBinding()
        dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                onStateDialogDismiss() -> {
                    dialog.dismiss()
                }
                else -> {}
            }
        }

        dialog.setOnShowListener {
            lifecycleOwner.lifecycle.addObserver(observer)
        }

        dialog.setOnDismissListener {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    /**
     * Override this function to change the dialog to close when the Lifecycle reaches a certain state default `Lifecycle.Event.ON_DESTROY`
     */
    protected open fun onStateDialogDismiss(): Lifecycle.Event {
        return Lifecycle.Event.ON_DESTROY
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