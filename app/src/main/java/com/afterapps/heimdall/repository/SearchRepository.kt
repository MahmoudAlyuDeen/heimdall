package com.afterapps.heimdall.repository

import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.network.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(private val shutterstockApi: ShutterstockApi) {

    suspend fun searchImages(query: String?): List<Result> {
        return withContext(Dispatchers.IO) {
            val resultsContainer = shutterstockApi.getShutterstockService().searchAsync(query).await()
            return@withContext resultsContainer.asDomainModel()
        }
    }
}