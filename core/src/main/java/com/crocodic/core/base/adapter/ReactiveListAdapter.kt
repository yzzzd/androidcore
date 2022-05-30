package com.crocodic.core.base.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.BR

/**
 * ### Ini makanan apa ?
 *
 * Sebagai pengganti CoreListAdapter
 *
 * Adapter ini cocok digunakan untuk membuat list yang posisi atau jumlah itemnya berubah-ubah,
 *
 * contoh ketika membuat list yang memerlukan aksi hapus item sehingga tidak perlu
 *
 * mengecek secara manual item yang hilang dari list dan tidak perlu memanggil method
 *
 * notifyItemChanged (thaks to DiffUtilCallback). Efek animasi perubahan item juga
 *
 * sudah dihandle secara otomatis
 *
 *```
 * ```
 * ### Fitur
 *
 * - Cukup dengan memanggil satu method submitList untuk memperbarui list item
 *
 * (tidak perlu lagi : clear list -> notifyDataSetChanged -> addAll -> notifyItemRangeChanged)
 * - Reactive terhadap perubahan list
 *
 * - Animasi perubahan list
 *
 *
 * ### Cara penggunaan adapter :
 * ```
 * // step 1 : inisialisasi adapter
 * val adapter = ReactiveListAdapter<ItemOrderBinding, Order>(R.layout.item_order)
 *
 * adapter.initItem { pos, data ->
 *   // handle onclick disini
 * }
 *
 * binding.rvOrder.adapter = adapter
 *
 *
 * // step 2 : perbarui list item
 * viewModel.orderList.observe(viewLifecycleowner) { list: List<Order> ->
 *      adapter.submitList(list)
 * }
 *
 * ```
 */
open class ReactiveListAdapter<VB: ViewDataBinding, T: Any>(private val layoutRes: Int) : ListAdapter<T, ReactiveListAdapter<VB, T>.ItemViewHolder<VB, T>>(DiffUtilCallback()){

    var onItemClick: ((position: Int, data: T) -> Unit)? = null

    open fun initItem(onItemClick: ((position: Int, data: T) -> Unit)? = null): ReactiveListAdapter<VB, T> {
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
        item?.let { itm ->
            holder.bind(itm)
            onItemClick?.let {
                holder.itemView.setOnClickListener { it(position, itm) }
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