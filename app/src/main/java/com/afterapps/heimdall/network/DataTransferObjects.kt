package com.afterapps.heimdall.network

import com.afterapps.heimdall.database.DatabaseCollection
import com.afterapps.heimdall.database.DatabaseImage
import com.afterapps.heimdall.util.getImageUrl

/** This file contains classes mapping API responses to objects */

data class CollectionContainer(val data: List<ShutterstockCollection>)

data class ImagesContainer(val data: List<ShutterstockImage>)

data class UrlContainer(val url: String)

data class ShutterstockCollection(
    val id: String,
    val name: String,
    val cover_item: UrlContainer,
    val share_url: String,
    val total_item_count: Int,
    val items_updated_time: String,
    val created_time: String,
    val updated_time: String
)

data class ShutterstockImage(
    val added_time: String,
    val id: String
)

/** Convert network results to database objects */

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





