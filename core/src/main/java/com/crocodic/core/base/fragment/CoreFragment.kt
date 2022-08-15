package com.crocodic.core.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.crocodic.core.helper.ImagePreviewHelper
import com.crocodic.core.helper.util.ClickPrevention

abstract class CoreFragment<VB : ViewDataBinding>(@LayoutRes private val layoutRes: Int) : Fragment(), ClickPrevention {

    protected var binding: VB? = null

    open var title: String = ""
    open var hasLoadedOnce = false

    val imagePreview by lazy { context?.let { ImagePreviewHelper(it) } }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        binding?.lifecycleOwner = this
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}