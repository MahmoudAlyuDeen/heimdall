package com.afterapps.heimdall.ui.collections

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afterapps.heimdall.MainCoroutineRule
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.repository.CollectionsRepository
import com.afterapps.heimdall.util.LiveDataTestUtil.getValue
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CollectionsViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var collectionsRepository: CollectionsRepository

    private lateinit var collectionsViewModel: CollectionsViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupCollectionsRepository() {
        MockKAnnotations.init(this, relaxed = true)

        val collections = MutableLiveData<List<Collection>>()
        coEvery { collectionsRepository.fetchCollections() } just runs
        every { collectionsRepository.collections } returns collections
        collectionsViewModel = CollectionsViewModel(collectionsRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_fetchCollections_success() {
        /** Collections returned from API after loading */
        val collectionsReturnedFromAPI = MutableLiveData<List<Collection>>()
        collectionsReturnedFromAPI.value = listOf(
            Collection(
                "1",
                name = "Name1",
                coverUrl = "CoverUrl1",
                shareUrl = "ShareUrl1",
                totalItemCount = 1,
                createdTime = "CreatedTime1",
                updatedTime = "UpdatedTime1"
            )
        )

        /** Pausing [CollectionsViewModel.fetchCollections] to test the initial loading status */
        mainCoroutineRule.pauseDispatcher()

        /** reinitializing to ensure [CollectionsViewModel.fetchCollections] is called */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** Progress is shown when [CollectionsRepository.collections] returns an empty list */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.LOADING)

        /** After API returns data, [CollectionsRepository.collections] is hydrated with collections */
        every { collectionsRepository.collections } returns collectionsReturnedFromAPI

        mainCoroutineRule.resumeDispatcher()

        /** Progress is hidden once [CollectionsRepository.collections] returns a list of collections */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.DONE)

        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun init_fetchCollections_cached() {
        /** Cached collections returned from [CollectionsRepository.collections] */
        val collections = MutableLiveData<List<Collection>>()
        collections.value = listOf(
            Collection(
                "1",
                name = "Name1",
                coverUrl = "CoverUrl1",
                shareUrl = "ShareUrl1",
                totalItemCount = 1,
                createdTime = "CreatedTime1",
                updatedTime = "UpdatedTime1"
            )
        )

        coEvery { collectionsRepository.fetchCollections() } just runs
        every { collectionsRepository.collections } returns collections

        /** reinitializing to ensure [CollectionsViewModel.fetchCollections] is called */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** Progress isn't shown when [CollectionsRepository.collections] returns cached collections */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.DONE)

        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun init_fetchCollections_error() {
        /** Cached collections returned from [CollectionsRepository.collections] */
        val collections = MutableLiveData<List<Collection>>()

        /** [CollectionsRepository.fetchCollections] throws exceptions for network and API errors */
        coEvery { collectionsRepository.fetchCollections() } throws Exception()
        every { collectionsRepository.collections } returns collections

        /** reinitializing to ensure [CollectionsViewModel.fetchCollections] is called */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** Show error when an exception is thrown */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.ERROR)

        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun imagesNavigationEvent() {
        val clickedCollection = Collection(
            "1",
            name = "Name1",
            coverUrl = "CoverUrl1",
            shareUrl = "ShareUrl1",
            totalItemCount = 1,
            createdTime = "CreatedTime1",
            updatedTime = "UpdatedTime1"
        )

        /** Initially, [CollectionsViewModel.eventNavigateToImages] contains null */
        assertThat(getValue(collectionsViewModel.eventNavigateToImages)).isEqualTo(null)

        collectionsViewModel.onCollectionClick(clickedCollection)

        /**
         * When [CollectionsViewModel.onCollectionClick] is called,
         * [CollectionsViewModel.eventNavigateToImages] contains ID
         */
        assertThat(getValue(collectionsViewModel.eventNavigateToImages)).isEqualTo(clickedCollection.id)

        collectionsViewModel.onNavigationToImagesDone()

        /**
         * When [CollectionsViewModel.onNavigationToImagesDone] is called,
         * [CollectionsViewModel.eventNavigateToImages] value is reset to null
         */
        assertThat(getValue(collectionsViewModel.eventNavigateToImages)).isEqualTo(null)
    }

    @Test
    fun searchNavigationEvent() {
        /** Initially, [CollectionsViewModel.eventNavigateToSearch] contains null */
        assertThat(getValue(collectionsViewModel.eventNavigateToSearch)).isEqualTo(null)

        collectionsViewModel.onSearchClick()

        /** When [CollectionsViewModel.onSearchClick] is called, [CollectionsViewModel.eventNavigateToSearch] true */
        assertThat(getValue(collectionsViewModel.eventNavigateToSearch)).isEqualTo(true)

        collectionsViewModel.onNavigationToSearchDone()

        /**
         * When [CollectionsViewModel.onNavigationToSearchDone] is called,
         * [CollectionsViewModel.eventNavigateToSearch] value is reset to null
         */
        assertThat(getValue(collectionsViewModel.eventNavigateToSearch)).isEqualTo(null)
    }
}