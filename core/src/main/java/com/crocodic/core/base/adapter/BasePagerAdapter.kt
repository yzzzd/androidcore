package com.crocodic.core.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crocodic.core.base.fragment.CoreFragment

class BasePagerAdapter<T : ViewDataBinding>(fa: FragmentActivity, private val fragments: List<CoreFragment<T>>) : FragmentStateAdapter(fa) {
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}