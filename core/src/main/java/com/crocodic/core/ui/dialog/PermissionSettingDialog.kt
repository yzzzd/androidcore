package com.crocodic.core.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.crocodic.core.databinding.CrDialogPermissionSettingBinding

/**
 * Created by @yzzzd on 4/22/18.
 */

class PermissionSettingDialog(context: Context): CoreBottomSheetDialog<CrDialogPermissionSettingBinding>(context) {

    override fun getViewBinding() = CrDialogPermissionSettingBinding.inflate(LayoutInflater.from(context))

    fun onButtonClick(onButtonClick: (dialog: PermissionSettingDialog) -> Unit): PermissionSettingDialog {
        binding.btnNeutral.setOnClickListener {
            onButtonClick(this)
        }
        return this
    }

    fun setContent(image: Int, message: Int): PermissionSettingDialog {
        setContent(image, string(message))
        return this
    }

    fun setContent(image: Int, message: String): PermissionSettingDialog {
        binding.ivImage.setImageResource(image)
        binding.tvMessage.text = message
        return this
    }

    fun hideButton(): PermissionSettingDialog {
        binding.btnNeutral.visibility = View.GONE
        return this
    }
}