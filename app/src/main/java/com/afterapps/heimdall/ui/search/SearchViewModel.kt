package com.afterapps.heimdall.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    /** Job and Scope to launch/cancel coroutines */
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /** Search query entered by user and a private backing property to prevent modification */
    private val _query = MutableLiveData<String>()
    val query: LiveData<String>
        get() = _query

    /** For each new _query, a SearchResult containing LiveData<PagedList<Results>> and LiveData<CallStatus> is created */
    private val searchResult = map(_query) {
        searchRepository.searchImages(it, viewModelScope)
    }

    /** Results displayed by the view */
    val results: LiveData<PagedList<Result>> = switchMap(searchResult) { it.resultsPagedList }

    /** Status displayed by the view */
    val status: LiveData<CallStatus?> = switchMap(searchResult) { it.status }

    /** Action event to open image in browser and a private backing property to prevent modification */
    private val _eventOpenInBrowser = MutableLiveData<String?>()
    val eventOpenInBrowser: LiveData<String?>
        get() = _eventOpenInBrowser

    fun onResultClick(result: Result) {
        _eventOpenInBrowser.value = result.websiteUrl
    }

    fun onOpenInBrowserDone() {
        _eventOpenInBrowser.value = null
    }

    /** Only update [_query] if newQuery is new and it didn't fail */
    fun onQuerySubmit(newQuery: String?) {
        if (newQuery == _query.value && status.value != CallStatus.ERROR) return
        _query.value = newQuery
    }

    /** Cancel any pending calls */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}