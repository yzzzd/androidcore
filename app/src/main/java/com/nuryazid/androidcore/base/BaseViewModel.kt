package com.nuryazid.androidcore.base

import androidx.lifecycle.viewModelScope
import com.crocodic.core.base.viewmodel.CoreViewModel
import kotlinx.coroutines.launch

open class BaseViewModel: CoreViewModel() {
    override fun apiRenewToken() = viewModelScope.launch {  }

    override fun apiLogout() = viewModelScope.launch {  }
}