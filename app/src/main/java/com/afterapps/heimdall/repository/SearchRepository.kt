package com.afterapps.heimdall.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.SEARCH_PAGE_SIZE
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.repository.paging.SearchDataSourceFactory
import kotlinx.coroutines.CoroutineScope

class SearchRepository(private val shutterstockApi: ShutterstockApi) {

    /** Construct a new [SearchDataSourceFactory] with the new [query] */
    fun searchImages(query: String?, scope: CoroutineScope): SearchResult {
        val factory = SearchDataSourceFactory(shutterstockApi, query, scope)
        val resultsPagedList = LivePagedListBuilder(factory, pagedListConfig()).build()

        return SearchResult(resultsPagedList, factory.status)
    }

    private fun pagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(SEARCH_PAGE_SIZE)
            .setPageSize(SEARCH_PAGE_SIZE)
            .build()
    }
}

/** Return type of [SearchRepository.searchImages], contains a [PagedList] of results and a [CallStatus] */
class SearchResult(val resultsPagedList: LiveData<PagedList<Result>>, val status: LiveData<CallStatus?>)
