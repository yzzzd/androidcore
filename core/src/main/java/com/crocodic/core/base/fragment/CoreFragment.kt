package com.crocodic.core.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.crocodic.core.helper.ClickPrevention
import com.crocodic.core.helper.ImagePreviewHelper

abstract class CoreFragment<VB : ViewDataBinding>: Fragment(), ClickPrevention {

    protected var binding: VB? = null

    open var title: String = ""
    open var hasLoadedOnce = false

    val imagePreview by lazy { context?.let { ImagePreviewHelper(it) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResources(), container, false)
        return binding?.root
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    protected abstract fun getLayoutResources(): Int
}