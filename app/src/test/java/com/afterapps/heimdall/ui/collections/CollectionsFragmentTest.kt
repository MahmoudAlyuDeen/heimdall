package com.afterapps.heimdall.ui.collections

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.afterapps.heimdall.R
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.network.CallStatus
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

@RunWith(RobolectricTestRunner::class)
@MediumTest
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
class CollectionsFragmentTest : AutoCloseKoinTest() {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var collectionsViewModel: CollectionsViewModel

    @Before
    fun setupCollectionsViewModel() {
        MockKAnnotations.init(this, relaxed = true)

        /** Mocking properties used by [CollectionsFragment]  */
        every { collectionsViewModel.collections } returns MutableLiveData()
        every { collectionsViewModel.eventNavigateToImages } returns MutableLiveData()
        every { collectionsViewModel.eventNavigateToSearch } returns MutableLiveData()
        every { collectionsViewModel.status } returns MutableLiveData()

        val mockModule = module {
            viewModel { collectionsViewModel }
        }

        startKoin {
            modules(mockModule)
        }
    }

    @Test
    fun progressBarVisible_loadingCollections() {
        val loadingStatus = MutableLiveData<CallStatus>()
        loadingStatus.value = CallStatus.LOADING
        every { collectionsViewModel.status } returns loadingStatus

        launchFragment()

        onView(withId(R.id.status_progress)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.status_image)).check(matches(not(isDisplayed())))
    }

    @Test
    fun errorViewVisible_loadingError() {
        val errorStatus = MutableLiveData<CallStatus>()
        errorStatus.value = CallStatus.ERROR
        every { collectionsViewModel.status } returns errorStatus

        launchFragment()

        onView(withId(R.id.status_image)).check(matches(isCompletelyDisplayed()))

        onView(withId(R.id.status_progress)).check(matches(not(isDisplayed())))
    }

    @Test
    fun displayCollections_cachedCollections() {
        val doneStatus = MutableLiveData<CallStatus>()
        doneStatus.value = CallStatus.DONE

        val collection = Collection(
            "1",
            name = "Name1",
            coverUrl = "CoverUrl1",
            shareUrl = "ShareUrl1",
            totalItemCount = 1,
            createdTime = "CreatedTime1",
            updatedTime = "UpdatedTime1"
        )

        val collections = MutableLiveData<List<Collection>>()
        collections.value = listOf(collection)

        every { collectionsViewModel.status } returns doneStatus
        every { collectionsViewModel.collections } returns collections

        launchFragment()

        onView(withId(R.id.status_image)).check(matches(not(isDisplayed())))
        onView(withId(R.id.status_progress)).check(matches(not(isDisplayed())))

        onView(withText(collection.name)).check(matches(isDisplayed()))
    }

    @Test
    fun onCollectionClick_cachedCollections() {
        val doneStatus = MutableLiveData<CallStatus>()
        doneStatus.value = CallStatus.DONE

        val collection = Collection(
            "1",
            name = "Name1",
            coverUrl = "CoverUrl1",
            shareUrl = "ShareUrl1",
            totalItemCount = 1,
            createdTime = "CreatedTime1",
            updatedTime = "UpdatedTime1"
        )

        val collections = MutableLiveData<List<Collection>>()
        collections.value = listOf(collection)

        val collectionSlot = CapturingSlot<Collection>()

        every { collectionsViewModel.status } returns doneStatus
        every { collectionsViewModel.collections } returns collections
        every { collectionsViewModel.onCollectionClick(capture(collectionSlot)) } just runs

        launchFragment()

        onView(withText(collection.name)).perform(click())

        verify {
            collectionsViewModel.onCollectionClick(collection)
        }
    }

    private fun launchFragment() {
        launchFragmentInContainer<CollectionsFragment>(null, R.style.AppTheme)
    }

}