package com.afterapps.heimdall.ui.collections

import android.view.MenuItem
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
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

    @MockK
    private lateinit var navController: NavController

    @MockK
    private lateinit var menuItem: MenuItem

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
    fun displaySingleCollection_cached() {
        val doneStatus = MutableLiveData<CallStatus>()
        doneStatus.value = CallStatus.DONE

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

    @Test
    fun onNavigationEvent_navigateToImages() {
        val doneStatus = MutableLiveData<CallStatus>()
        doneStatus.value = CallStatus.DONE

        val collections = MutableLiveData<List<Collection>>()
        val eventNavigateToImages: MutableLiveData<String> = MutableLiveData<String>(null)

        collections.value = listOf(collection)

        every { collectionsViewModel.status } returns doneStatus
        every { collectionsViewModel.collections } returns collections
        every { collectionsViewModel.eventNavigateToImages } returns eventNavigateToImages
        every { navController.currentDestination?.id } returns R.id.collectionsFragment

        val scenario = launchFragment()
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        // WHEN
        onView(withText(collection.name)).perform(click())
        eventNavigateToImages.value = collection.id

        // THEN
        verify {
            collectionsViewModel.onNavigationToImagesDone()
            navController.navigate(
                CollectionsFragmentDirections.navigateToImages(collection.id),
                any<FragmentNavigator.Extras>()
            )
        }
    }

    @Test
    fun onNavigationEvent_navigateToSearch() {
        val eventNavigateToSearch: MutableLiveData<Boolean> = MutableLiveData<Boolean>(null)

        every { collectionsViewModel.eventNavigateToSearch } returns eventNavigateToSearch
        every { navController.currentDestination?.id } returns R.id.collectionsFragment

        val scenario = launchFragment()
        scenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        // WHEN
        eventNavigateToSearch.value = true

        // THEN
        verify {
            collectionsViewModel.onNavigationToSearchDone()
            navController.navigate(CollectionsFragmentDirections.navigateToSearch())
        }
    }

    @Test
    fun onSearchOptionsItemSelected() {
        val scenario = launchFragment()
        every { menuItem.itemId } returns R.id.action_start_search

        // WHEN
        scenario.onFragment { it.onOptionsItemSelected(menuItem) }

        // THEN
        verify {
            collectionsViewModel.onSearchClick()
        }
    }

    @Test
    fun onOtherOptionsItemSelected() {
        val scenario = launchFragment()
        every { menuItem.itemId } returns Integer.MAX_VALUE

        // WHEN
        scenario.onFragment { it.onOptionsItemSelected(menuItem) }

        // THEN
        verify(exactly = 0) {
            collectionsViewModel.onSearchClick()
        }
    }

    private fun launchFragment() =
        launchFragmentInContainer<CollectionsFragment>(null, R.style.AppTheme)

}