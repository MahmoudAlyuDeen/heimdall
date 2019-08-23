package com.afterapps.heimdall.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.CallStatus.*
import com.afterapps.heimdall.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    /** Job and Scope to launch/cancel coroutines */
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /** Search query entered by user */
    private var query: String? = null

    /** Results displayed by the view and a private backing property to prevent modification */
    private val _results = MutableLiveData<List<Result>>()
    val results: LiveData<List<Result>>
        get() = _results

    /** Call status displayed by the view and a private backing property to prevent modification */
    private val _status = MutableLiveData<CallStatus>()
    val status: LiveData<CallStatus>
        get() = _status

    /** Action event to open image in browser and a private backing property to prevent modification */
    private val _eventOpenInBrowser = MutableLiveData<String?>()
    val eventOpenInBrowser: LiveData<String?>
        get() = _eventOpenInBrowser

    /** Fetches results from API and updates view status accordingly */
    private fun fetchResults() {
        viewModelScope.launch {
            try {
                _status.value = LOADING
                _results.value = searchRepository.searchImages(query)
                _status.value = if (_results.value.isNullOrEmpty()) EMPTY else DONE
            } catch (e: Exception) {
                _status.value = ERROR
            }
        }
    }

    fun onResultClick(result: Result) {
        _eventOpenInBrowser.value = result.websiteUrl
    }

    fun onOpenInBrowserDone() {
        _eventOpenInBrowser.value = null
    }

    /** When the user submits a new query, clear the old results */
    fun onQuerySubmit(newQuery: String?) {
        if (newQuery == query && !_results.value.isNullOrEmpty()) return
        query = newQuery
        clearResults()
        fetchResults()
    }

    private fun clearResults() {
        _results.value = emptyList()
    }

    /** Cancel any pending calls */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}