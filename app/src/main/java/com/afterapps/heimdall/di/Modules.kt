package com.afterapps.heimdall.di

import androidx.room.Room
import com.afterapps.heimdall.database.ShutterstockDatabase
import com.afterapps.heimdall.network.ShutterstockApi
import com.afterapps.heimdall.repository.CollectionsRepository
import com.afterapps.heimdall.repository.ImagesRepository
import com.afterapps.heimdall.ui.collections.CollectionsViewModel
import com.afterapps.heimdall.ui.images.ImagesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** API and Database dependencies */
val dataModule = module {

    single { ShutterstockApi() }

    single {
        Room.databaseBuilder(get(), ShutterstockDatabase::class.java, "shutterstockdatabase").build()
    }
}

/** ViewModel and Repository dependencies for the Collections view */
val collectionsModule = module {

    single { CollectionsRepository(shutterstockDatabase = get(), shutterstockApi = get()) }

    viewModel { CollectionsViewModel(collectionsRepository = get()) }

}

/** ViewModel and Repository dependencies for the Images view */
val imagesModule = module {

    single {
        ImagesRepository(shutterstockDatabase = get(), shutterstockApi = get())
    }

    viewModel { (collectionId: String) ->
        ImagesViewModel(imagesRepository = get(), collectionsRepository = get(), collectionId = collectionId)
    }

}