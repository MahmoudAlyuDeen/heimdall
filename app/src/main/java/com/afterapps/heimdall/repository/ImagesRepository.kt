package com.afterapps.heimdall.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.afterapps.heimdall.database.ShutterstockDatabase
import com.afterapps.heimdall.database.asDomainModel
import com.afterapps.heimdall.domain.Image
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImagesRepository(
    private val shutterstockDatabase: ShutterstockDatabase,
    private val shutterstockApi: ShutterstockApi
) {

    fun getImages(collectionId: String): LiveData<List<Image>> =
        Transformations.map(shutterstockDatabase.imagesDao.getImages()) {
            it.asDomainModel(collectionId)
        }

    suspend fun fetchImages(collectionId: String) {
        withContext(Dispatchers.IO) {
            val imagesContainer = shutterstockApi.getShutterstockService().getImagesAsync(collectionId).await()
            shutterstockDatabase.imagesDao.insertImages(imagesContainer.asDatabaseModel(collectionId))
        }
    }
}