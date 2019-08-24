package com.afterapps.heimdall.repository.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.CallStatus.*
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.network.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SearchDataSource(
    private val shutterstockApi: ShutterstockApi,
    private val query: String?,
    private val scope: CoroutineScope
) :
    PageKeyedDataSource<Int, Result>() {

    /** Status exposed to the [SearchDataSourceFactory] and a private backing property to prevent modification */
    private val _status = MutableLiveData<CallStatus>()
    val status: LiveData<CallStatus>
        get() = _status

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Result>) {
        val currentPage = 1
        val nextPage = currentPage + 1

        scope.launch {
            try {
                _status.value = LOADING
                val resultsContainer = shutterstockApi
                    .getShutterstockService()
                    .searchAsync(query, currentPage, params.requestedLoadSize)
                    .await()
                val results = resultsContainer.asDomainModel()
                _status.value = if (results.isNullOrEmpty()) EMPTY else DONE
                callback.onResult(results, currentPage, nextPage)
            } catch (e: Exception) {
                _status.value = ERROR
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Result>) {
        val currentPage = params.key
        val nextPage = currentPage + 1

        scope.launch {
            try {
                _status.value = ADDING
                val resultsContainer = shutterstockApi
                    .getShutterstockService()
                    .searchAsync(query, currentPage, params.requestedLoadSize)
                    .await()
                val results = resultsContainer.asDomainModel()
                _status.value = DONE
                callback.onResult(results, nextPage)
            } catch (e: Exception) {
                _status.value = DONE
            }
        }
    }

    /** Not used because subsequent loads only add to the PagedList */
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Result>) = Unit

}