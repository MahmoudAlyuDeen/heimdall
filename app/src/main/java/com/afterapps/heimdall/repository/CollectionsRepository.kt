package com.afterapps.heimdall.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.afterapps.heimdall.database.ShutterstockDatabase
import com.afterapps.heimdall.database.asDomainModel
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CollectionsRepository(
    private val shutterstockDatabase: ShutterstockDatabase,
    private val shutterstockApi: ShutterstockApi
) {

    val collections: LiveData<List<Collection>> =
        Transformations.map(shutterstockDatabase.collectionDao.getCollections()) {
            it.sortedBy { collection -> collection.name }.asDomainModel()
        }

    suspend fun fetchCollections() {
        withContext(Dispatchers.IO) {
            val collectionContainer = shutterstockApi.getShutterstockService().getFeaturedCollectionAsync().await()
            shutterstockDatabase.collectionDao.insertCollections(collectionContainer.asDatabaseModel())
        }
    }
}