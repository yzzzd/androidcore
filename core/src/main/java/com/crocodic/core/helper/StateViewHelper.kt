package com.crocodic.core.helper

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.crocodic.core.BR
import com.crocodic.core.R
import com.crocodic.core.widget.stateview.StateView

/**
 * Created by @yzzzd on 4/19/18.
 */

class StateViewHelper(val stateView: StateView?, @LayoutRes emptyRes: Int = R.layout.state_empty, @LayoutRes val errorRes: Int = R.layout.state_error, @LayoutRes loadingRes: Int = R.layout.state_loading) {
    init {
        setState(STATE_EMPTY, emptyRes)
        setState(STATE_ERROR, errorRes)
        setState(STATE_LOADING, loadingRes)
    }

    fun showContent(state: String = StateView.CONTENT) {
        stateView?.setState(state)
    }

    fun showEmpty(value: Boolean) {
        if (value) {
            stateView?.setState(STATE_EMPTY)
        } else {
            showContent()
        }
    }

    fun showLoading() {
        stateView?.setState(STATE_LOADING)
    }

    fun showError(message: String? = null) {
        if (message != null && stateView != null) {
            val binding = stateView.generateViewBinding<ViewDataBinding>(errorRes)
            binding.setVariable(BR.data, message)
            stateView.setStateView(STATE_ERROR, binding.root)
        }

        stateView?.setState(STATE_ERROR)
    }

    fun setState(state: String, @LayoutRes layoutRes: Int) {
        stateView?.setStateView(state, stateView.generateViewBinding<ViewDataBinding>(layoutRes).root)
    }

    companion object {
        const val STATE_EMPTY = "empty"
        const val STATE_LOADING = "loading"
        const val STATE_ERROR = "error"

        inline fun <reified T : ViewDataBinding> View.generateViewBinding(@LayoutRes layout: Int): T {
            return DataBindingUtil.inflate(LayoutInflater.from(this.context), layout, null, false)
        }
    }
}