package com.afterapps.heimdall.repository.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.ShutterstockApi
import kotlinx.coroutines.CoroutineScope

class SearchDataSourceFactory(
    private val shutterstockApi: ShutterstockApi,
    private val query: String?,
    private val scope: CoroutineScope
) : DataSource.Factory<Int, Result>() {

    private val dataSource = MutableLiveData<SearchDataSource>()

    /** Status exposed to the SearchRepository and a private backing property to prevent modification */
    private val _status = Transformations.switchMap(dataSource) { it.status }
    val status: LiveData<CallStatus?>
        get() = _status

    override fun create(): DataSource<Int, Result> {
        val searchDataSource = SearchDataSource(shutterstockApi, query, scope)
        dataSource.postValue(searchDataSource)
        return searchDataSource
    }
}