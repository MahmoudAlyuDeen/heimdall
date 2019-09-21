package com.afterapps.heimdall.network

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before

class ShutterstockApiTest {

    @MockK
    private lateinit var shutterstockService: ShutterstockService

    @Before
    fun setupApiService() {
        MockKAnnotations.init(this, relaxed = true)
    }



}