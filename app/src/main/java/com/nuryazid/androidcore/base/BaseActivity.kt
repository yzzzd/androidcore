package com.nuryazid.androidcore.base

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.base.viewmodel.CoreViewModel

open class BaseActivity<VB : ViewDataBinding, VM : CoreViewModel>(@LayoutRes private val layoutRes: Int): CoreActivity<VB, VM>(layoutRes)