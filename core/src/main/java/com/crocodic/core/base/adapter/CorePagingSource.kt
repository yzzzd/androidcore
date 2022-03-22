package com.crocodic.core.base.adapter

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Dibangun diatas library paging3
 *
 * Menggunakan [PaginationAdapter] sebagai ganti pengganti CoreListAdapter
 *
 * [firstPageIndex] Halaman pertama paginasi
 *
 * [block] Suspend function dengan return value List <T>
 *
 * [config] Set jumlah item per halaman, mengembalikan PagingConfig sebagai param untuk object Pager
 *
 * Contoh penggunanaan :
 * ```
 * =================================================================================================
 *  // Repository
 *  suspend fun getOrderList(page: Int, limit: Int): Flow<List<Order>>
 * =================================================================================================
 *  // ViewModel
 *  private val firstPageIndex = 1
 *
 *  fun getOrderPaging(): Flow<PagingData<Order>> = Pager(CorePagingSource.config(itemPerPage=10), pagingSourceFactory = {
 *      CorePagingSource(firstPageIndex){ page, limit ->
 *          repository.getOrderList(page= page, limit= limit ).first()
 *      }
 *  }).flow.cachedIn(viewModelScope)
 * =================================================================================================
 * // Fragment/Activity
 * val adapter = PaginationAdapter<ItemOrderBinding, Order>(R.layout.item_order).initItem { pos, data ->
 *      // handle item click disini
 * }
 * binding?.rvOrder?.adapter = adapter
 * viewLifecycleOwner.lifecycleScope.launch {
 *     order.collect { adapter.submitData(it) }
 * }
 * ```
 */
open class CorePagingSource<T : Any> constructor(
    private val firstPageIndex: Int,
    private val block: suspend (page: Int, limit: Int) -> List<T>
): PagingSource<Int, T>(){

    companion object {
        fun config(itemPerPage: Int = 10) = PagingConfig(pageSize = itemPerPage, initialLoadSize = itemPerPage)
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: firstPageIndex
            val data = block(page, params.loadSize)
            val nextPage = if (data.size < params.loadSize) {
                null
            } else {
                page + 1
            }
            val prevPage = if (page == firstPageIndex) null else page - 1
            return LoadResult.Page(
                data = data,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}