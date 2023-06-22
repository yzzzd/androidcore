package com.crocodic.core.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.crocodic.core.databinding.CrDialogExpiredBinding

/**
 * Created by @yzzzd on 4/22/18.
 */

class ExpiredDialog(context: Context, onButtonClick: (positive: Boolean, dialog: ExpiredDialog) -> Unit) : CoreBottomSheetDialog<CrDialogExpiredBinding>(context) {

    override fun getViewBinding() = CrDialogExpiredBinding.inflate(LayoutInflater.from(context))

    private var stillLoading = false

    init {
        binding.btnOk.setOnClickListener {
            if (!stillLoading) {
                onButtonClick(true, this)
            }
        }
        binding.btnCancel.setOnClickListener {
            if (!stillLoading) {
                onButtonClick(false, this)
            }
        }
    }

    fun setLoading(isLoading: Boolean = true, isPositive: Boolean = true): ExpiredDialog {

        stillLoading = isLoading

        if (isLoading) {
            binding.vContent.alpha = 0.25f
            binding.progressWheel.visibility = View.VISIBLE

            if (isPositive) {
                binding.btnOk.alpha = 0.5f
            } else {
                binding.btnCancel.alpha = 0.5f
            }

        } else {
            binding.vContent.alpha = 1f
            binding.btnOk.alpha = 1f
            binding.btnCancel.alpha = 1f
            binding.progressWheel.visibility = View.GONE
        }

        return this
    }

    override fun show(): CoreBottomSheetDialog<CrDialogExpiredBinding> {
        if (isShowing() && stillLoading) {
            setLoading(false)
        }

        if (!isShowing()) {
            setCancelable(false)
            return super.show()
        }
        return this
    }

    override fun dismiss(): CoreBottomSheetDialog<CrDialogExpiredBinding> {
        if (isShowing()) {
            setLoading(false)
        }
        return super.dismiss()
    }
}
