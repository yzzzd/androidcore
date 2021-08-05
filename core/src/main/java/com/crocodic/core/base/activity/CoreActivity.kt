package com.crocodic.core.base.activity

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.crocodic.core.base.viewmodel.CoreViewModel
import java.lang.reflect.ParameterizedType

abstract class CoreActivity<VB : ViewDataBinding, VM : CoreViewModel> : NoViewModelActivity<VB>() {

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModelClass = (javaClass
            .genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>

        viewModel = ViewModelProvider(this).get(viewModelClass)
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