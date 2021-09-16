package com.crocodic.core.helper

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.crocodic.core.R
import com.crocodic.core.widget.stateview.StateView

/**
 * Created by @yzzzd on 4/19/18.
 */

class StateViewHelper(val stateView: StateView?, @LayoutRes emptyRes: Int = R.layout.state_empty, @LayoutRes loadingRes: Int = R.layout.state_loading) {
    init {
        stateView?.setStateView(STATE_EMPTY, stateView.generateViewBinding<ViewDataBinding>(emptyRes).root)
        stateView?.setStateView(STATE_LOADING, stateView.generateViewBinding<ViewDataBinding>(loadingRes).root)
    }

    fun showContent() {
        stateView?.setState(StateView.CONTENT)
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

    companion object {
        const val STATE_EMPTY = "empty"
        const val STATE_LOADING = "loading"

        inline fun <reified T : ViewDataBinding> View.generateViewBinding(@LayoutRes layout: Int): T {
            return DataBindingUtil.inflate(LayoutInflater.from(this.context), layout, null, false)
        }
    }
}