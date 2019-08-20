package com.afterapps.heimdall.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.afterapps.heimdall.domain.Image
import com.afterapps.heimdall.repository.ImagesRepository


class GalleryViewModel(
    imagesRepository: ImagesRepository,
    collectionId: String,
    initialImageId: String
) : ViewModel() {

    /** Images displayed by the view and a private backing property to prevent modification */
    private val _images = imagesRepository.getImages(collectionId)
    val images: LiveData<List<Image>>
        get() = _images

    /** Initial image position is the position of the image the user clicked */
    private val initialImagePosition = Transformations.map(_images) {
        _images.value?.indexOfFirst { it.id == initialImageId }
    }

    /** Currently displayed image position and a private backing property to prevent modification */
    private val _currentImagePosition: MutableLiveData<Int?> = initialImagePosition as MutableLiveData<Int?>
    val currentImagePosition = _currentImagePosition

    fun onPageChanged(position: Int) {
        _currentImagePosition.value = position
    }
}