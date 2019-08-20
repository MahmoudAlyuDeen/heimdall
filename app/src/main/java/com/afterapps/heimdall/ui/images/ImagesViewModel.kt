package com.afterapps.heimdall.ui.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.domain.Image
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.CallStatus.*
import com.afterapps.heimdall.repository.CollectionsRepository
import com.afterapps.heimdall.repository.ImagesRepository
import com.afterapps.heimdall.util.getCallStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class ImagesViewModel(
    private val imagesRepository: ImagesRepository,
    collectionsRepository: CollectionsRepository,
    private val collectionId: String
) : ViewModel() {

    /** Job and Scope to launch/cancel coroutines */
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /** Images displayed by the view and a private backing property to prevent modification */
    private val _images = imagesRepository.getImages(collectionId)
    val images: LiveData<List<Image>>
        get() = _images

    /** Collection loaded from the database and a private backing property to prevent modification */
    private val _collection = collectionsRepository.getCollection(collectionId)
    val collection: LiveData<Collection?>
        get() = _collection

    /** Call status displayed by the view and a private backing property to prevent modification */
    private val _status = MediatorLiveData<CallStatus>()
    val status: LiveData<CallStatus>
        get() = _status

    /** Navigation event to Gallery view and a private backing property to prevent modification */
    private val _eventNavigateToGallery = MutableLiveData<Pair<String, String>>()
    val eventNavigateToGallery: LiveData<Pair<String, String>>
        get() = _eventNavigateToGallery

    /** Action event to open collection in browser and a private backing property to prevent modification */
    private val _eventOpenInBrowser = MutableLiveData<String?>()
    val eventOpenInBrowser: LiveData<String?>
        get() = _eventOpenInBrowser

    init {
        fetchImages()
        initImagesObserver()
    }

    /** Fetches Images from API and updates view status accordingly */
    private fun fetchImages() {
        viewModelScope.launch {
            try {
                _status.value = if (_images.value.isNullOrEmpty()) LOADING else DONE
                imagesRepository.fetchImages(collectionId)
                _status.value = DONE
            } catch (e: Exception) {
                _status.value = if (_images.value.isNullOrEmpty()) ERROR else DONE
            }
        }
    }

    /** Observe ImagesRepository.images to display correct loading/error state */
    private fun initImagesObserver() {
        _status.addSource(_images) { _status.value = getCallStatus(it, _status.value) }
    }

    fun onImageClick(image: Image) {
        _eventNavigateToGallery.value = Pair(collectionId, image.id)
    }

    /** Resetting event value to avoid navigating more than once */
    fun onNavigationToGalleryDone() {
        _eventNavigateToGallery.value = null
    }

    fun onOpenInBrowserClick() {
        _eventOpenInBrowser.value = collection.value?.shareUrl
    }

    /** Resetting event value to avoid performing action more than once */
    fun onOpenInBrowserDone() {
        _eventOpenInBrowser.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}