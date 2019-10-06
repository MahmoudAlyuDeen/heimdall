package com.afterapps.heimdall.ui.collections

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.afterapps.heimdall.MainCoroutineRule
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.repository.CollectionsRepository
import com.afterapps.heimdall.util.valueSync
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

    private val collection = Collection(
        "1",
        name = "Name1",
        coverUrl = "CoverUrl1",
        shareUrl = "ShareUrl1",
        totalItemCount = 1,
        createdTime = "CreatedTime1",
        updatedTime = "UpdatedTime1"
    )

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
        /** GIVEN - a list of collections containing 1 element */
        val collectionsReturnedFromAPI = MutableLiveData<List<Collection>>()
        collectionsReturnedFromAPI.value = listOf(collection)

        /** AFTER - Pausing [CollectionsViewModel.fetchCollections] to test the initial loading status */
        mainCoroutineRule.pauseDispatcher()

        /** WHEN - [CollectionsViewModel] is instantiated */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** THEN - progress is shown when [CollectionsRepository.collections] returns an empty list */
        assertThat(collectionsViewModel.status.valueSync).isEqualTo(CallStatus.LOADING)

        /** WHEN - after API returns data, [CollectionsRepository.collections] is hydrated with collections */
        every { collectionsRepository.collections } returns collectionsReturnedFromAPI

        mainCoroutineRule.resumeDispatcher()

        /** THEN - progress is hidden once [CollectionsRepository.collections] returns a list of collections */
        assertThat(collectionsViewModel.status.valueSync).isEqualTo(CallStatus.DONE)
        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun init_fetchCollections_cached() {
        /** GIVEN - cached collections returned from [CollectionsRepository.collections] */
        val collections = MutableLiveData<List<Collection>>()
        collections.value = listOf(collection)

        coEvery { collectionsRepository.fetchCollections() } just runs
        every { collectionsRepository.collections } returns collections

        /** WHEN - [CollectionsViewModel] is instantiated */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** THEN - progress isn't shown when [CollectionsRepository.collections] returns cached collections */
        assertThat(collectionsViewModel.status.valueSync).isEqualTo(CallStatus.DONE)

        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun init_fetchCollections_error() {
        /** GIVEN - [CollectionsRepository.fetchCollections] throws exceptions for network and API errors */
        val collections = MutableLiveData<List<Collection>>()

        coEvery { collectionsRepository.fetchCollections() } throws Exception()
        every { collectionsRepository.collections } returns collections

        /** WHEN - [CollectionsViewModel] is instantiated */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** THEN - [CollectionsViewModel.status].value = [CallStatus.ERROR] */
        assertThat(collectionsViewModel.status.valueSync).isEqualTo(CallStatus.ERROR)
        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun imagesNavigationEvent() {
        /** GIVEN - initially, [CollectionsViewModel.eventNavigateToImages] contains null */
        assertThat(collectionsViewModel.eventNavigateToImages.valueSync).isEqualTo(null)

        /** WHEN - a collection is clicked */
        collectionsViewModel.onCollectionClick(collection)

        /** THEN - [CollectionsViewModel.eventNavigateToImages] contains the collection's id */
        assertThat(collectionsViewModel.eventNavigateToImages.valueSync).isEqualTo(collection.id)

        /** WHEN - [CollectionsViewModel.onNavigationToImagesDone] is called */
        collectionsViewModel.onNavigationToImagesDone()

        /** THEN - [CollectionsViewModel.eventNavigateToImages].value is reset to null */
        assertThat(collectionsViewModel.eventNavigateToImages.valueSync).isEqualTo(null)
    }

    @Test
    fun searchNavigationEvent() {
        /** GIVEN - initially, [CollectionsViewModel.eventNavigateToSearch] contains null */
        assertThat(collectionsViewModel.eventNavigateToSearch.valueSync).isEqualTo(null)

        /** WHEN - search menu item is clicked */
        collectionsViewModel.onSearchClick()

        /** THEN - [CollectionsViewModel.eventNavigateToSearch].value = true to signal navigation */
        assertThat(collectionsViewModel.eventNavigateToSearch.valueSync).isEqualTo(true)

        /** WHEN - [CollectionsViewModel.onNavigationToSearchDone] is called */
        collectionsViewModel.onNavigationToSearchDone()

        /** THEN - [CollectionsViewModel.eventNavigateToSearch].value is reset to null */
        assertThat(collectionsViewModel.eventNavigateToSearch.valueSync).isEqualTo(null)
    }
}