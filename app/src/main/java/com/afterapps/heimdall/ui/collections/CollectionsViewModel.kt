package com.afterapps.heimdall.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.CallStatus.*
import com.afterapps.heimdall.repository.CollectionsRepository
import com.afterapps.heimdall.util.getCallStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CollectionsViewModel(private val collectionsRepository: CollectionsRepository) : ViewModel() {

    /** Job and Scope to launch/cancel coroutines */
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /** Collections displayed by the view and a private backing property to prevent modification */
    private val _collections = collectionsRepository.collections
    val collections: LiveData<List<Collection>>
        get() = _collections

    /** Call status displayed by the view and a private backing property to prevent modification */
    private val _status = MediatorLiveData<CallStatus>()
    val status: LiveData<CallStatus>
        get() = _status

    /** Navigation event to Images view and a private backing property to prevent modification */
    private val _eventNavigateToImages = MutableLiveData<String>()
    val eventNavigateToImages: LiveData<String>
        get() = _eventNavigateToImages

    /** Navigation event to Search view and a private backing property to prevent modification */
    private val _eventNavigateToSearch = MutableLiveData<Boolean>()
    val eventNavigateToSearch: LiveData<Boolean>
        get() = _eventNavigateToSearch

    init {
        fetchCollections()
        initCollectionsObserver()
    }

    /** Fetches collections from API and updates view status accordingly */
    private fun fetchCollections() {
        viewModelScope.launch {
            try {
                _status.value = if (_collections.value.isNullOrEmpty()) LOADING else DONE
                collectionsRepository.fetchCollections()
                _status.value = DONE
            } catch (e: Exception) {
                _status.value = if (_collections.value.isNullOrEmpty()) ERROR else DONE
            }
        }
    }

    /** Observe CollectionsRepository.collections to display correct loading/error state */
    private fun initCollectionsObserver() {
        _status.addSource(_collections) { _status.value = getCallStatus(it, _status.value) }
    }

    /** Sets event value to collection ID to signal that the view should navigate */
    fun onCollectionClick(collection: Collection) {
        _eventNavigateToImages.value = collection.id
    }

    /** Resetting event value to avoid navigating more than once */
    fun onNavigationToImagesDone() {
        _eventNavigateToImages.value = null
    }

    /** Sets event value to true to signal that the view should navigate */
    fun onSearchClick() {
        _eventNavigateToSearch.value = true
    }

    /** Resetting event value to avoid navigating more than once */
    fun onNavigationToSearchDone() {
        _eventNavigateToSearch.value = null
    }

    /** Cancel any pending calls */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}