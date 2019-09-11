package com.afterapps.heimdall.database

import com.afterapps.heimdall.domain.Image
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DatabaseEntitiesTest {

    @Test
    fun databaseCollectionsAsDomainModel() {
        val databaseCollection = DatabaseCollection(
            "1",
            name = "Name1",
            coverUrl = "CoverUrl1",
            shareUrl = "ShareUrl1",
            totalItemCount = 1,
            createdTime = "CreatedTime1",
            updatedTime = "UpdatedTime1"
        )

        val collections = listOf(databaseCollection)

        val domainCollection = collections.asDomainModel()[0]

        assertThat(domainCollection).isNotNull()

        assertThat(databaseCollection.id).isEqualTo(domainCollection.id)
        assertThat(databaseCollection.name).isEqualTo(domainCollection.name)
        assertThat(databaseCollection.coverUrl).isEqualTo(domainCollection.coverUrl)
        assertThat(databaseCollection.shareUrl).isEqualTo(domainCollection.shareUrl)
        assertThat(databaseCollection.totalItemCount).isEqualTo(domainCollection.totalItemCount)
        assertThat(databaseCollection.createdTime).isEqualTo(domainCollection.createdTime)
        assertThat(databaseCollection.updatedTime).isEqualTo(domainCollection.updatedTime)
    }

    @Test
    fun databaseImageAsDomainModel() {
        val collectionId = "1"

        val databaseImage = DatabaseImage(
            "1",
            imageUrl = "ImageUrl1",
            thumbnailUrl = "ThumbnailUrl1",
            collectionId = collectionId
        )

        val images = listOf(databaseImage)

        val domainImage: Image = images.asDomainModel(collectionId)[0]

        assertThat(domainImage).isNotNull()

        assertThat(databaseImage.id).isEqualTo(domainImage.id)
        assertThat(databaseImage.imageUrl).isEqualTo(domainImage.imageUrl)
        assertThat(databaseImage.thumbnailUrl).isEqualTo(domainImage.thumbnailUrl)
        assertThat(databaseImage.collectionId).isEqualTo(domainImage.collectionId)
    }

}