package com.crocodic.core.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.crocodic.core.R
import com.crocodic.core.databinding.CrItemBottomListBinding

/**
 * Created by @yzzzd on 4/22/18.
 */

class CoreBottomListAdapter<T: Any?>(val items: List<T?>, val onItemClick: (data: T?) -> Unit) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val viewHolder: ItemViewHolder<T>?

        if (convertView == null) {
            val binding = DataBindingUtil.inflate<CrItemBottomListBinding>(LayoutInflater.from(parent?.context), R.layout.cr_item_bottom_list, parent, false)
            viewHolder = ItemViewHolder(binding)
            viewHolder.view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ItemViewHolder<T>
        }

        viewHolder.bind(items[position])
        viewHolder.view.setOnClickListener { onItemClick(items[position]) }

        return viewHolder.view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = items.size

    class ItemViewHolder<T: Any?>(val binding: CrItemBottomListBinding) {
        val view = binding.root
        fun bind(data: T?) {
            binding.data = data.toString()
            binding.executePendingBindings()
        }
    }
}