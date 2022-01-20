package com.crocodic.core.base.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.crocodic.core.helper.util.ClickPrevention

/**
 * Created by @yzzzd on 4/19/18.
 */

open class SingleClickListAdapter<VB: ViewDataBinding, T: Any?>(private var layoutRes: Int) : CoreListAdapter<VB, T>(layoutRes) {
    override fun onBindViewHolder(holder: ItemViewHolder<VB, T>, position: Int) {
        items[holder.bindingAdapterPosition]?.let { item ->
            holder.bind(item)
            onItemClick?.let {
                holder.itemView.setOnClickListener(object : ClickPrevention {
                    override fun onClick(v: View?) {
                        it(holder.bindingAdapterPosition, item)
                        super.onClick(v)
                    }
                })
            }
        }
    }
}