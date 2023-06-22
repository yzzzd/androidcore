package com.crocodic.core.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.crocodic.core.databinding.CrDialogInformationBinding

/**
 * Created by @yzzzd on 4/22/18.
 */

class InformationDialog(context: Context, lifecycleOwner: LifecycleOwner): CoreBottomSheetDialog<CrDialogInformationBinding>(context, lifecycleOwner) {

    override fun getViewBinding() = CrDialogInformationBinding.inflate(LayoutInflater.from(context))

    fun setMessage(@StringRes message: Int, @DrawableRes drawable: Int): InformationDialog {
        return setMessage(null, string(message), drawable)
    }

    fun setMessage(@StringRes title: Int, @StringRes message: Int, @DrawableRes drawable: Int): InformationDialog {
        return setMessage(string(title), string(message), drawable)
    }

    fun setMessage(title: CharSequence?, message: CharSequence, @DrawableRes drawable: Int): InformationDialog {
        if (title == null) {
            binding.tvTitle.visibility = View.GONE
        } else {
            binding.tvTitle.visibility = View.VISIBLE
            binding.tvTitle.text = title
        }
        binding.tvMessage.text = message
        binding.ivImage.setImageResource(drawable)
        return this
    }

    fun showButton(show: Boolean): InformationDialog {
        setCancelable(show)
        binding.ivClose.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }
}
