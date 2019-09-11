package com.afterapps.heimdall.network

import com.afterapps.heimdall.database.DatabaseCollection
import com.afterapps.heimdall.database.DatabaseImage
import com.afterapps.heimdall.domain.Result
import com.afterapps.heimdall.util.getImageUrl

/** This file contains classes mapping API responses to objects */

data class CollectionContainer(val data: List<ShutterstockCollection>)

data class ImagesContainer(val data: List<ShutterstockImage>)

data class ResultsContainer(val data: List<ShutterstockResult>)

data class UrlContainer(val url: String)

data class ShutterstockCollection(
    val id: String,
    val name: String,
    val cover_item: UrlContainer = UrlContainer(""),
    val share_url: String,
    val total_item_count: Int,
    val created_time: String,
    val updated_time: String
)

data class ShutterstockImage(
    val id: String
)

data class ShutterstockResult(
    val description: String,
    val id: String
)

/** Convert network results to database and domain objects */

fun CollectionContainer.asDatabaseModel(): List<DatabaseCollection> {
    return data.map {
        DatabaseCollection(
            id = it.id,
            name = it.name,
            coverUrl = it.cover_item.url,
            shareUrl = it.share_url,
            totalItemCount = it.total_item_count,
            createdTime = it.created_time,
            updatedTime = it.updated_time
        )
    }
}

fun ImagesContainer.asDatabaseModel(collectionId: String): List<DatabaseImage> {
    return data.map {
        DatabaseImage(
            id = it.id,
            imageUrl = getImageUrl(imageUrlFormat, it.id),
            thumbnailUrl = getImageUrl(thumbnailUrlFormat, it.id),
            collectionId = collectionId
        )
    }
}

fun ResultsContainer.asDomainModel(): List<Result> {
    return data.map {
        Result(
            id = it.id,
            imageUrl = getImageUrl(imageUrlFormat, it.id),
            thumbnailUrl = getImageUrl(thumbnailUrlFormat, it.id),
            websiteUrl = getImageUrl(imageWebsiteUrlFormat, it.id),
            description = it.description.trim()
        )
    }
}