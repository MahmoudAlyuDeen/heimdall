package com.afterapps.heimdall.ui.collections

import android.util.Log
import androidx.lifecycle.ViewModel
import com.afterapps.heimdall.repository.CollectionsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CollectionsViewModel(private val collectionsRepository: CollectionsRepository) : ViewModel() {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val collections = collectionsRepository.collections

    init {
        fetchCollections()
    }

    private fun fetchCollections() {
        viewModelScope.launch {
            try {
                collectionsRepository.fetchCollections()
            } catch (e: Exception) {
                Log.d("fetchCollections, e", e.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}