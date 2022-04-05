package com.crocodic.core.base.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.crocodic.core.R
import com.crocodic.core.databinding.StateLoadingPaginationBinding
import com.crocodic.core.BR
/**
 * Paging3 adapter memiliki method :
 *
 * - withLoadStateFooter
 *
 * - withLoadStateHeader
 *
 * - withLoadStateHeaderAndFooter
 *
 * yang dapat digunakan untuk menampilkan loading pada header dan footer
 *
 *```
 *```
 * ### Default loading
 *
 * ```
 * val adapter = PaginationAdapter<ItemProductBinding, Product>(R.layout.item_product)
 * with(adapter){
 *      binding.rvProduct.adapter = withLoadStateFooter(PaginationLoadState.default)
 * }
 *
 * ```
 *
 * ### Custom loading
 *
 * Untuk sebagian besar kasus cukup dengan menggunakan default loading, namun jika
 * ada kebutuhan untuk custom loading dapat dengan menggunakan cara berikut
 * 1. Buatlah layout loading baru, didalamnya **harus ada variabel bernama "loading" dengan tipe Boolean**, contoh : state_loading_custom.xml
 * ```
 * <layout>
 *
 *     <data>
 *         <variable
 *              name="loading"
 *              type="Boolean" />
 *
 *     </data>
 *     <LinearLayout>
 *          // gunakan value loading untuk mengubah view
 *     </LinearLayout>
 * </layout>
 * ```
 *
 * *Catatan: Contoh layout di [R.layout.state_loading_pagination]*
 *
 *
 * 2. Implementasikan pada recyclerview
 *
 * ```
 * val adapter = PaginationAdapter<ItemProductBinding, Product>(R.layout.item_product)
 * with(adapter){
 *      val loadStateView = PaginationLoadState<StateLoadingCustomBinding>(R.layout.state_loading_custom)
 *      binding.rvProduct.adapter = withLoadStateFooter(loadStateView)
 * }
 * ```
 */
open class PaginationLoadState<VB : ViewDataBinding>(private val layoutRes: Int) :
    LoadStateAdapter<PaginationLoadState<VB>.NetworkStateItemViewHolder<VB>>() {

    companion object {
        val default = PaginationLoadState<StateLoadingPaginationBinding>(R.layout.state_loading_pagination)
    }

    inner class NetworkStateItemViewHolder<VB : ViewDataBinding>(private val binding: VB) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            val state = loadState is LoadState.Loading
            binding.setVariable(BR.loading, state)
        }
    }

    override fun onBindViewHolder(holder: NetworkStateItemViewHolder<VB>, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkStateItemViewHolder<VB> {
        val binding = DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), layoutRes, parent, false)
        return NetworkStateItemViewHolder(binding)
    }

}