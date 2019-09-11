package com.afterapps.heimdall.network

import com.afterapps.heimdall.util.getImageUrl
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DataTransferObjectsTest {

    @Test
    fun collectionContainerAsDatabaseModel() {
        val coverItem = UrlContainer(
            url = "url1"
        )

        val collection = ShutterstockCollection(
            "1",
            name = "Name1",
            cover_item = coverItem,
            share_url = "ShareUrl1",
            total_item_count = 1,
            created_time = "CreatedTime1",
            updated_time = "UpdatedTime1"
        )

        val collectionContainer = CollectionContainer(listOf(collection))

        val databaseCollection = collectionContainer.asDatabaseModel()[0]

        assertThat(databaseCollection).isNotNull()

        assertThat(collection.id).isEqualTo(databaseCollection.id)
        assertThat(collection.name).isEqualTo(databaseCollection.name)
        assertThat(collection.cover_item.url).isEqualTo(databaseCollection.coverUrl)
        assertThat(collection.share_url).isEqualTo(databaseCollection.shareUrl)
        assertThat(collection.total_item_count).isEqualTo(databaseCollection.totalItemCount)
        assertThat(collection.created_time).isEqualTo(databaseCollection.createdTime)
        assertThat(collection.updated_time).isEqualTo(databaseCollection.updatedTime)
    }

    @Test
    fun imagesContainerAsDatabaseModel() {
        val collectionId = "1"
        val image = ShutterstockImage(id = "1")

        val imagesContainer = ImagesContainer(listOf(image))

        val databaseImage = imagesContainer.asDatabaseModel(collectionId)[0]

        assertThat(databaseImage).isNotNull()

        assertThat(databaseImage.id).isEqualTo(image.id)
        assertThat(databaseImage.collectionId).isEqualTo(collectionId)
        assertThat(databaseImage.thumbnailUrl).isEqualTo(getImageUrl(thumbnailUrlFormat, image.id))
        assertThat(databaseImage.imageUrl).isEqualTo(getImageUrl(imageUrlFormat, image.id))
    }

    @Test
    fun resultsContainerAsDomainModel() {
        val result = ShutterstockResult(id = "1", description = "Description1")

        val resultsContainer = ResultsContainer(listOf(result))

        val domainResult = resultsContainer.asDomainModel()[0]

        assertThat(domainResult).isNotNull()

        assertThat(domainResult.id).isEqualTo(result.id)
        assertThat(domainResult.description).isEqualTo(result.description)
        assertThat(domainResult.thumbnailUrl).isEqualTo(getImageUrl(thumbnailUrlFormat, result.id))
        assertThat(domainResult.imageUrl).isEqualTo(getImageUrl(imageUrlFormat, result.id))
        assertThat(domainResult.websiteUrl).isEqualTo(getImageUrl(imageWebsiteUrlFormat, result.id))
    }

}