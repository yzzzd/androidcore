package com.crocodic.core.base.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.BR

/**
 * Dibangun diatas library paging3
 *
 * Cara inisialisasi adapter :
 * ```
 * val adapter = PaginationAdapter<ItemOrderBinding, Order>(R.layout.item_order).initItem { pos, data ->
 *      // handle onclick disini
 * }
 * ```
 *
 * Untuk mengambil list item didalam adapter :
 * ```
 * val orderList = adapter.snapshot()
 *
 * ```
 * Panduan untuk membuat object Pager dapat dilihat di doc [CorePagingSource]
 */
open class PaginationAdapter<VB: ViewDataBinding, T: Any>(private val layoutRes: Int) : PagingDataAdapter<T, PaginationAdapter<VB, T>.ItemViewHolder<VB, T>>(DiffUtilCallback()){

    var onItemClick: ((position: Int, data: T) -> Unit)? = null

    open fun initItem(onItemClick: ((position: Int, data: T) -> Unit)? = null): PaginationAdapter<VB, T> {
        this.onItemClick = onItemClick
        return this
    }

    inner class ItemViewHolder<VB : ViewDataBinding, T: Any?>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: T?) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<VB, T>, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item)
            onItemClick?.let {
                holder.itemView.setOnClickListener {
                    it(position, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<VB, T> {
        val binding = DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), layoutRes, parent, false)
        return ItemViewHolder(binding)
    }

    class DiffUtilCallback<T: Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return newItem === oldItem
        }
    }
}