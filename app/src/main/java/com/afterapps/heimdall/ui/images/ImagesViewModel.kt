package com.afterapps.heimdall.ui.images

import android.util.Log
import androidx.lifecycle.ViewModel
import com.afterapps.heimdall.repository.ImagesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ImagesViewModel(private val imagesRepository: ImagesRepository, private val collectionId: String) : ViewModel() {

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val images = imagesRepository.getImages(collectionId)

    init {
        fetchImages()
    }

    private fun fetchImages() {
        viewModelScope.launch {
            try {
                imagesRepository.fetchImages(collectionId)
            } catch (e: Exception) {
                Log.d("fetchImages, e", e.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}