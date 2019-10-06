package com.afterapps.heimdall.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afterapps.heimdall.database.DatabaseImage
import com.afterapps.heimdall.database.ShutterstockDatabase
import com.afterapps.heimdall.domain.Image
import com.afterapps.heimdall.network.ImagesContainer
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.network.ShutterstockImage
import com.afterapps.heimdall.util.valueSync
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ImagesRepositoryTest {

    @MockK
    private lateinit var shutterstockApi: ShutterstockApi

    @MockK
    private lateinit var shutterstockDatabase: ShutterstockDatabase

    private lateinit var imagesRepository: ImagesRepository

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val networkImage = ShutterstockImage("1")

    private val databaseImage = DatabaseImage(
        "1",
        "https://image.shutterstock.com/z/-1.jpg",
        "https://image.shutterstock.com/image-photo/-260nw-1.jpg",
        "1"
    )

    private val modelImage = Image(
        "1",
        "https://image.shutterstock.com/z/-1.jpg",
        "https://image.shutterstock.com/image-photo/-260nw-1.jpg",
        "1"
    )

    @Before
    fun setupTest() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun getImagesForCollection() {
        /** GIVEN - The [ShutterstockDatabase] contains a [DatabaseImage] with [DatabaseImage.collectionId] = "1" */
        val databaseImages = MutableLiveData(listOf(databaseImage))
        every { shutterstockDatabase.imagesDao.getImages(databaseImage.id) } returns databaseImages

        /** WHEN - [ImagesRepository.getImages] is called with param "1" */
        imagesRepository = ImagesRepository(shutterstockDatabase, shutterstockApi)
        val images = imagesRepository.getImages(databaseImage.collectionId)

        /** THEN - [ImagesRepository] returns a list of images filtered by [DatabaseImage.collectionId] */
        assertThat(images.valueSync).isEqualTo(listOf(modelImage))
    }

    @Test
    fun fetchImagesAndSaveToDatabase() = runBlocking {
        /** GIVEN - [ShutterstockApi] returns a list containing images */
        coEvery {
            shutterstockApi.getShutterstockService().getImagesAsync(modelImage.collectionId).await()
        } returns ImagesContainer(listOf(networkImage))

        /** WHEN - [ImagesRepository.fetchImages] is called with param "1"  */
        imagesRepository = ImagesRepository(shutterstockDatabase, shutterstockApi)
        imagesRepository.fetchImages(modelImage.collectionId)

        /** THEN - [ShutterstockDatabase.imagesDao] is called to persist the fetched images */
        verify { shutterstockDatabase.imagesDao.insertImages(listOf(databaseImage)) }
    }
}