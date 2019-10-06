package com.afterapps.heimdall.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afterapps.heimdall.database.DatabaseCollection
import com.afterapps.heimdall.database.ShutterstockDatabase
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.network.CollectionContainer
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.network.ShutterstockCollection
import com.afterapps.heimdall.network.UrlContainer
import com.afterapps.heimdall.util.LiveDataTestUtil.getValue
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
class CollectionsRepositoryTest {

    @MockK
    private lateinit var shutterstockApi: ShutterstockApi

    @MockK
    private lateinit var shutterstockDatabase: ShutterstockDatabase

    private lateinit var collectionsRepository: CollectionsRepository

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val networkCollection = ShutterstockCollection(
        "1",
        "Name1",
        UrlContainer("CoverUrl1"),
        "ShareUrl1",
        1,
        "CreatedTime1",
        "UpdatedTime1"
    )

    private val databaseCollection = DatabaseCollection(
        "1",
        "Name1",
        "CoverUrl1",
        "ShareUrl1",
        1,
        "CreatedTime1",
        "UpdatedTime1"
    )

    private val modelCollection = Collection(
        "1",
        "Name1",
        "CoverUrl1",
        "ShareUrl1",
        1,
        "CreatedTime1",
        "UpdatedTime1"
    )

    @Before
    fun setupTest() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun loadCollectionsFromDatabase() {
        /** GIVEN - The [ShutterstockDatabase] contains a collection */
        val collections = MutableLiveData(listOf(databaseCollection))
        every { shutterstockDatabase.collectionsDao.getCollections() } returns collections

        /** WHEN - [CollectionsRepository] is instantiated */
        collectionsRepository = CollectionsRepository(shutterstockDatabase, shutterstockApi)

        /** THEN - [CollectionsRepository.collections].value is a list of all collections in the database */
        assertThat(getValue(collectionsRepository.collections)).isEqualTo(listOf(modelCollection))
    }

    @Test
    fun fetchCollectionAndSaveToDatabase() = runBlocking {
        /** GIVEN - [ShutterstockApi] returns a list containing featured collection */
        coEvery {
            shutterstockApi.getShutterstockService().getFeaturedCollectionAsync().await()
        } returns CollectionContainer(listOf(networkCollection))

        /** WHEN - [CollectionsRepository.fetchCollections] is called */
        collectionsRepository = CollectionsRepository(shutterstockDatabase, shutterstockApi)
        collectionsRepository.fetchCollections()

        /** THEN - [ShutterstockDatabase.collectionsDao] is called to persist the fetched collections */
        verify { shutterstockDatabase.collectionsDao.insertCollections(listOf(databaseCollection)) }
    }

    @Test
    fun getSingleCollection() {
        /** GIVEN - The [ShutterstockDatabase] contains a [DatabaseCollection] */
        val collections = MutableLiveData(listOf(databaseCollection))
        every { shutterstockDatabase.collectionsDao.getCollections() } returns collections

        /** WHEN - [CollectionsRepository.getCollection] is called */
        collectionsRepository = CollectionsRepository(shutterstockDatabase, shutterstockApi)
        val collection = collectionsRepository.getCollection(modelCollection.id)

        /** THEN - [CollectionsRepository] returns the requested collection filtered by [Collection.id] */
        assertThat(getValue(collection)).isEqualTo(modelCollection)
    }
}