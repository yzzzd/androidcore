package com.crocodic.core.base.activity

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.crocodic.core.base.viewmodel.CoreViewModel
import java.lang.reflect.ParameterizedType

/**
 * Created by @yzzzd on 4/22/18.
 */

abstract class CoreActivity<VB : ViewDataBinding, VM : CoreViewModel>(@LayoutRes private val layoutRes: Int) : NoViewModelActivity<VB>(layoutRes) {

    protected val viewModel: VM by lazy {
        val viewModelClass = (javaClass
            .genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>

        ViewModelProvider(this)[viewModelClass]
    }

    override fun authRenewToken() {
        viewModel.apiRenewToken()
    }

    override fun authLogoutRequest() {
        viewModel.apiLogout()
    }

    companion object {
        object EVENT {
            const val RENEW_TOKEN = "renew_token"
            const val LOGGED_OUT = "logged_out"
        }
    }
}