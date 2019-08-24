package com.afterapps.heimdall.network

import com.afterapps.heimdall.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.shutterstock.com/v2/"

/** ShutterStock URLs for image, full size images, and thumbnails with placeholders for image IDs */
const val imageUrlFormat = "https://image.shutterstock.com/z/-%s.jpg"
const val thumbnailUrlFormat = "https://image.shutterstock.com/image-photo/-260nw-%s.jpg"
const val imageWebsiteUrlFormat = "https://shutterstock.com/image/%s"

/** Search qualifier with sensible defaults */
private const val SEARCH_QUALIFIER = "view=minimal&spellcheck_query=true&sort=relevance"

/** The number of results fetched from the API every paging request */
const val SEARCH_PAGE_SIZE = 10

/** Adding interceptor to add authorization header to API calls */
private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.ShutterstockApiToken}")
            .build()
        it.proceed(request)
    }.build()

interface ShutterstockService {

    @GET("images/collections/featured?embed=share_url")
    fun getFeaturedCollectionAsync(): Deferred<CollectionContainer>

    @GET("images/collections/featured/{id}/items")
    fun getImagesAsync(@Path("id") collectionId: String): Deferred<ImagesContainer>

    @GET("images/search?$SEARCH_QUALIFIER")
    fun searchAsync(
        @Query("query") query: String?,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Deferred<ResultsContainer>

}

/**
 * Main entry point to ShutterStock API
 * Ex: ShutterstockApi().getShutterstockService().getFeaturedCollectionAsync()
 */
class ShutterstockApi {

    fun getShutterstockService(): ShutterstockService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        return retrofit.create(ShutterstockService::class.java)
    }
}

enum class CallStatus { LOADING, ADDING, DONE, ERROR, EMPTY }