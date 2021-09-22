package com.crocodic.core.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.BR
import com.crocodic.core.R

/**
 * Created by @yzzzd on 4/19/18.
 */

@Deprecated("Akan diganti dengan CoreListAdapter")
open class BaseListAdapter<VB: ViewDataBinding, T: Any?>(private var layoutRes: Int) : RecyclerView.Adapter<BaseListAdapter.ItemViewHolder<VB, T>>() {

    var items: ArrayList<T?> = ArrayList()
    var onItemClick: ((position: Int, data: T?) -> Unit)? = null

    fun initItem(items: ArrayList<T?>, onItemClick: ((position: Int, data: T?) -> Unit)? = null) : BaseListAdapter<VB, T> {
        this.items = items
        this.onItemClick = onItemClick
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<VB, T> {
        val binding = DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), if (viewType == VIEW_TYPE_LOADING) R.layout.cr_item_load_more else layoutRes, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder<VB, T>, position: Int) {
        items[position]?.let { item ->
            holder.bind(item)
            onItemClick?.let { holder.itemView.setOnClickListener { it(position, item) } }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    fun addNull() {
        if (!items.contains(null)) {
            items.add(null)
            notifyDataSetChanged()
            //notifyItemInserted(items.lastIndex)
        }
    }

    fun removeNull() {
        if (items.contains(null)) {
            items.remove(null)
            notifyDataSetChanged()
            //notifyItemRemoved(items.size)
        }
    }

    class ItemViewHolder<VB : ViewDataBinding, T: Any?>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: T?) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }

    companion object {
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_ITEM = 1
        fun RecyclerView.Adapter<RecyclerView.ViewHolder>.get() = this as BaseListAdapter<*, *>
        fun RecyclerView.ViewHolder.get() = this as ItemViewHolder<*, *>
    }
}
