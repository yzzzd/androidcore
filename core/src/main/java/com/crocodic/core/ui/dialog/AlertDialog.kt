package com.crocodic.core.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import com.crocodic.core.databinding.CrDialogAlertBinding

/**
 * Created by @yzzzd on 4/22/18.
 */

class AlertDialog(context: Context, onButtonClick: (positive: Boolean, dialog: AlertDialog) -> Unit) : CoreBottomSheetDialog<CrDialogAlertBinding>(context) {

    override fun getViewBinding() = CrDialogAlertBinding.inflate(LayoutInflater.from(context))

    init {
        binding.btnOk.setOnClickListener { onButtonClick(true, this) }
        binding.btnCancel.setOnClickListener { onButtonClick(false, this) }
    }

    fun setButton(@StringRes strPositive: Int, @StringRes strNegative: Int): AlertDialog {
        return setButton(string(strPositive), string(strNegative))
    }

    fun setButton(strPositive: String, strNegative: String): AlertDialog {
        binding.btnOk.text = strPositive
        binding.btnCancel.text = strNegative
        return this
    }

    fun setIcon(icon: Int): AlertDialog {
        binding.ivResponse.setImageResource(icon)
        binding.ivResponse.visibility = View.VISIBLE
        return this
    }

    fun show(@StringRes message: Int): AlertDialog {
        binding.tvMessage.text = string(message)
        show()
        return this
    }
}
