package com.crocodic.core.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.crocodic.core.R
import com.crocodic.core.databinding.CrDialogLoadingBinding

/**
 * Created by @yzzzd on 4/22/18.
 */

class LoadingDialog(context: Context): CoreDialog<CrDialogLoadingBinding>(context) {

    override fun getViewBinding() = CrDialogLoadingBinding.inflate(LayoutInflater.from(context))

    fun setMessage(@StringRes message: Int): LoadingDialog {
        return setMessage(string(message))
    }

    fun setMessage(message: CharSequence): LoadingDialog {
        binding.tvMessage.text = message
        binding.tvMessage.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.ivResponse.visibility = View.GONE
        return this
    }

    fun setResponse(@StringRes message: Int, @DrawableRes resImage: Int): LoadingDialog {
        return setResponse(string(message), resImage)
    }

    fun setResponse(@StringRes message: Int): LoadingDialog {
        return setResponse(string(message))
    }

    fun setResponse(message: String): LoadingDialog {
        return setResponse(message, R.drawable.ic_case_negative)
    }

    fun setResponse(message: String, @DrawableRes resImage: Int): LoadingDialog {
        setCancelable(true)
        binding.tvMessage.text = message
        binding.ivResponse.setImageResource(resImage)

        binding.tvTitle.visibility = View.GONE
        binding.tvMessage.visibility = View.VISIBLE
        binding.ivResponse.visibility = View.VISIBLE
        binding.progressWheel.visibility = View.GONE

        show()
        return this
    }

    fun show(@StringRes message: Int): LoadingDialog {
        return show(string(message))
    }

    fun show(message: String): LoadingDialog {
        binding.progressWheel.visibility = View.VISIBLE
        binding.tvMessage.text = message
        binding.tvMessage.visibility = View.VISIBLE
        binding.tvTitle.visibility = View.GONE
        binding.ivResponse.visibility = View.GONE
        setCancelable(false)
        show()
        return this
    }
}