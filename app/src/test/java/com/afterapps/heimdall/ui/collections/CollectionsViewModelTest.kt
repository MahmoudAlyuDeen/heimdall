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
        /** GIVEN - a list of collections containing 1 element */

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

        /** AFTER - Pausing [CollectionsViewModel.fetchCollections] to test the initial loading status */
        mainCoroutineRule.pauseDispatcher()

        /** WHEN - reinitializing to ensure [CollectionsViewModel.fetchCollections] is called */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** THEN - progress is shown when [CollectionsRepository.collections] returns an empty list */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.LOADING)

        /** WHEN - after API returns data, [CollectionsRepository.collections] is hydrated with collections */
        every { collectionsRepository.collections } returns collectionsReturnedFromAPI

        mainCoroutineRule.resumeDispatcher()

        /** THEN - progress is hidden once [CollectionsRepository.collections] returns a list of collections */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.DONE)
        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun init_fetchCollections_cached() {
        /** GIVEN - cached collections returned from [CollectionsRepository.collections] */
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

        /** WHEN - reinitializing to ensure [CollectionsViewModel.fetchCollections] is called */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** THEN - progress isn't shown when [CollectionsRepository.collections] returns cached collections */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.DONE)

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

        /** WHEN */
        /** reinitializing to ensure [CollectionsViewModel.fetchCollections] is called */
        collectionsViewModel = CollectionsViewModel(collectionsRepository)

        /** THEN - [CollectionsViewModel.status].value = [CallStatus.ERROR] */
        assertThat(getValue(collectionsViewModel.status)).isEqualTo(CallStatus.ERROR)
        coVerifyAll {
            collectionsRepository.collections
            collectionsRepository.fetchCollections()
        }
    }

    @Test
    fun imagesNavigationEvent() {
        /** GIVEN - a collection */

        val clickedCollection = Collection(
            "1",
            name = "Name1",
            coverUrl = "CoverUrl1",
            shareUrl = "ShareUrl1",
            totalItemCount = 1,
            createdTime = "CreatedTime1",
            updatedTime = "UpdatedTime1"
        )

        /** GIVEN - initially, [CollectionsViewModel.eventNavigateToImages] contains null */
        assertThat(getValue(collectionsViewModel.eventNavigateToImages)).isEqualTo(null)

        /** WHEN - a collection is clicked */
        collectionsViewModel.onCollectionClick(clickedCollection)

        /** THEN - [CollectionsViewModel.eventNavigateToImages] contains the collection's id */
        assertThat(getValue(collectionsViewModel.eventNavigateToImages)).isEqualTo(clickedCollection.id)

        /** WHEN - [CollectionsViewModel.onNavigationToImagesDone] is called */
        collectionsViewModel.onNavigationToImagesDone()

        /** THEN - [CollectionsViewModel.eventNavigateToImages].value is reset to null */
        assertThat(getValue(collectionsViewModel.eventNavigateToImages)).isEqualTo(null)
    }

    @Test
    fun searchNavigationEvent() {
        /** GIVEN - initially, [CollectionsViewModel.eventNavigateToSearch] contains null */
        assertThat(getValue(collectionsViewModel.eventNavigateToSearch)).isEqualTo(null)

        /** WHEN - search menu item is clicked */
        collectionsViewModel.onSearchClick()

        /** THEN - [CollectionsViewModel.eventNavigateToSearch].value = true to signal navigation */
        assertThat(getValue(collectionsViewModel.eventNavigateToSearch)).isEqualTo(true)

        /** WHEN - [CollectionsViewModel.onNavigationToSearchDone] is called */
        collectionsViewModel.onNavigationToSearchDone()

        /** THEN - [CollectionsViewModel.eventNavigateToSearch].value is reset to null */
        assertThat(getValue(collectionsViewModel.eventNavigateToSearch)).isEqualTo(null)
    }
}