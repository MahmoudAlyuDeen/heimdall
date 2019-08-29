package com.afterapps.heimdall.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.domain.Image

@Entity
data class DatabaseCollection (
    @PrimaryKey
    val id: String,
    val name: String,
    val coverUrl: String,
    val shareUrl: String,
    val totalItemCount: Int,
    val createdTime: String,
    val updatedTime: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = DatabaseCollection::class,
        parentColumns = ["id"],
        childColumns = ["collectionId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class DatabaseImage (
    @PrimaryKey
    val id: String,
    val imageUrl: String,
    val thumbnailUrl: String,
    val collectionId: String
)

/** Convert Database objects to domain objects to be displayed or manipulated */

fun List<DatabaseCollection>.asDomainModel(): List<Collection> {
    return map {
        Collection(
            id = it.id,
            name = it.name,
            coverUrl = it.coverUrl,
            shareUrl = it.shareUrl,
            totalItemCount = it.totalItemCount,
            createdTime = it.createdTime,
            updatedTime = it.updatedTime
        )
    }
}

fun List<DatabaseImage>.asDomainModel(collectionId: String): List<Image> {
    return map {
        Image(
            id = it.id,
            imageUrl = it.imageUrl,
            thumbnailUrl = it.thumbnailUrl,
            collectionId = collectionId
        )
    }
}