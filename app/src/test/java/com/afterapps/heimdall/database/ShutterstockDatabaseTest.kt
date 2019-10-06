package com.afterapps.heimdall.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.afterapps.heimdall.util.valueSync
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShutterstockDatabaseTest {

    /** Executing each task synchronously using Architecture Components */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ShutterstockDatabase

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ShutterstockDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertAndGetCollections() {
        val collection = DatabaseCollection(
            id = "1",
            name = "name1",
            coverUrl = "coverUrl1",
            shareUrl = "shareUrl1",
            totalItemCount = 1,
            createdTime = "createdTime1",
            updatedTime = "updatedTime1"
        )

        database.collectionsDao.insertCollections(listOf(collection))

        val loaded = database.collectionsDao.getCollections()

        assertThat(loaded.valueSync[0].id).isEqualTo(collection.id)
        assertThat(loaded.valueSync[0].name).isEqualTo(collection.name)
        assertThat(loaded.valueSync[0].coverUrl).isEqualTo(collection.coverUrl)
        assertThat(loaded.valueSync[0].shareUrl).isEqualTo(collection.shareUrl)
        assertThat(loaded.valueSync[0].totalItemCount).isEqualTo(collection.totalItemCount)
        assertThat(loaded.valueSync[0].createdTime).isEqualTo(collection.createdTime)
        assertThat(loaded.valueSync[0].updatedTime).isEqualTo(collection.updatedTime)
    }

    @Test
    fun insertCollectionsReplaceOnConflict() {
        val collection = DatabaseCollection(
            id = "1",
            name = "name1",
            coverUrl = "coverUrl1",
            shareUrl = "shareUrl1",
            totalItemCount = 1,
            createdTime = "createdTime1",
            updatedTime = "updatedTime1"
        )

        database.collectionsDao.insertCollections(listOf(collection))

        /** Same [DatabaseCollection.id] */
        val newCollection = DatabaseCollection(
            id = "1",
            name = "name2",
            coverUrl = "coverUrl2",
            shareUrl = "shareUrl2",
            totalItemCount = 2,
            createdTime = "createdTime2",
            updatedTime = "updatedTime2"
        )

        database.collectionsDao.insertCollections(listOf(newCollection))

        val loaded = database.collectionsDao.getCollections()

        assertThat(loaded.valueSync[0].id).isEqualTo(newCollection.id)
        assertThat(loaded.valueSync[0].name).isEqualTo(newCollection.name)
        assertThat(loaded.valueSync[0].coverUrl).isEqualTo(newCollection.coverUrl)
        assertThat(loaded.valueSync[0].shareUrl).isEqualTo(newCollection.shareUrl)
        assertThat(loaded.valueSync[0].totalItemCount).isEqualTo(newCollection.totalItemCount)
        assertThat(loaded.valueSync[0].createdTime).isEqualTo(newCollection.createdTime)
        assertThat(loaded.valueSync[0].updatedTime).isEqualTo(newCollection.updatedTime)
    }

    @Test
    fun insertAndGetImages() {
        /** [DatabaseImage] has a foreign key containing [DatabaseCollection.id] */
        val collection = DatabaseCollection(
            id = "1",
            name = "name1",
            coverUrl = "coverUrl1",
            shareUrl = "shareUrl1",
            totalItemCount = 1,
            createdTime = "createdTime1",
            updatedTime = "updatedTime1"
        )

        database.collectionsDao.insertCollections(listOf(collection))

        val image = DatabaseImage(
            id = "1",
            imageUrl = "imageUrl1",
            thumbnailUrl = "thumbnailUrl1",
            collectionId = collection.id
        )

        database.imagesDao.insertImages(listOf(image))

        val loaded = database.imagesDao.getImages(collection.id)

        assertThat(loaded.valueSync[0].id).isEqualTo(image.id)
        assertThat(loaded.valueSync[0].imageUrl).isEqualTo(image.imageUrl)
        assertThat(loaded.valueSync[0].thumbnailUrl).isEqualTo(image.thumbnailUrl)
        assertThat(loaded.valueSync[0].collectionId).isEqualTo(image.collectionId)
    }

    @Test
    fun insertImagesReplaceOnConflict() {
        /** [DatabaseImage] has a foreign key containing [DatabaseCollection.id] */
        val oldCollection = DatabaseCollection(
            id = "1",
            name = "name1",
            coverUrl = "coverUrl1",
            shareUrl = "shareUrl1",
            totalItemCount = 1,
            createdTime = "createdTime1",
            updatedTime = "updatedTime1"
        )

        val newCollection = DatabaseCollection(
            id = "2",
            name = "name2",
            coverUrl = "coverUrl2",
            shareUrl = "shareUrl2",
            totalItemCount = 2,
            createdTime = "createdTime2",
            updatedTime = "updatedTime2"
        )

        database.collectionsDao.insertCollections(listOf(oldCollection, newCollection))

        val image = DatabaseImage(
            id = "1",
            imageUrl = "imageUrl1",
            thumbnailUrl = "thumbnailUrl1",
            collectionId = oldCollection.id
        )

        database.imagesDao.insertImages(listOf(image))

        /** Same [DatabaseImage.id] */
        val newImage = DatabaseImage(
            id = "1",
            imageUrl = "imageUrl2",
            thumbnailUrl = "thumbnailUrl2",
            collectionId = newCollection.id
        )

        database.imagesDao.insertImages(listOf(newImage))

        val oldCollectionImages = database.imagesDao.getImages(oldCollection.id)

        /** [DatabaseImage] is no longer associated with the old [DatabaseCollection] */
        assertThat(oldCollectionImages.valueSync.isNullOrEmpty()).isTrue()

        val newCollectionImages = database.imagesDao.getImages(newCollection.id)

        assertThat(newCollectionImages.valueSync[0].id).isEqualTo(newImage.id)
        assertThat(newCollectionImages.valueSync[0].imageUrl).isEqualTo(newImage.imageUrl)
        assertThat(newCollectionImages.valueSync[0].thumbnailUrl).isEqualTo(newImage.thumbnailUrl)
        assertThat(newCollectionImages.valueSync[0].collectionId).isEqualTo(newImage.collectionId)
    }
}