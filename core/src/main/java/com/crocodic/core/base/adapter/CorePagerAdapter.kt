package com.crocodic.core.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crocodic.core.base.fragment.CoreFragment

/**
 * Created by @yzzzd on 4/22/18.
 */

class CorePagerAdapter<T : ViewDataBinding>(fa: FragmentActivity, private val fragments: List<CoreFragment<T>>) : FragmentStateAdapter(fa) {
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}