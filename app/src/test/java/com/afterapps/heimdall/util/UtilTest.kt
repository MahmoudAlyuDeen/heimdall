package com.afterapps.heimdall.util

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.network.imageUrlFormat
import com.afterapps.heimdall.network.imageWebsiteUrlFormat
import com.afterapps.heimdall.network.thumbnailUrlFormat
import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.Test

class UtilTest {

    @Test
    fun getImageUrl_imageUrlFormat() {
        val imageId = "test"
        val url = getImageUrl(imageUrlFormat, imageId)

        assertThat(url, `is`("https://image.shutterstock.com/z/-test.jpg"))
    }

    @Test
    fun getImageUrl_thumbnailUrlFormat() {
        val imageId = "test"
        val url = getImageUrl(thumbnailUrlFormat, imageId)

        assertThat(url, `is`("https://image.shutterstock.com/image-photo/-260nw-test.jpg"))
    }

    @Test
    fun getImageUrl_imageWebsiteUrlFormat() {
        val imageId = "test"
        val url = getImageUrl(imageWebsiteUrlFormat, imageId)

        assertThat(url, `is`("https://shutterstock.com/image/test"))
    }

    @Test
    fun getCachedResourceStatus_null() {
        val callStatus = null
        val resource = null

        val status = getCachedResourceStatus(resource, callStatus)

        Assert.assertEquals(status, null)
    }

    @Test
    fun getCachedResourceStatus_loadingIfNull() {
        val callStatus = CallStatus.LOADING
        val resource = null

        val status = getCachedResourceStatus(resource, callStatus)

        Assert.assertEquals(status, CallStatus.LOADING)
    }

    @Test
    fun getCachedResourceStatus_loadingIfEmpty() {
        val callStatus = CallStatus.LOADING
        val resource = ArrayList<Any>()

        val status = getCachedResourceStatus(resource, callStatus)

        Assert.assertEquals(status, CallStatus.LOADING)
    }

    @Test
    fun getCachedResourceStatus_done() {
        val callStatus = CallStatus.LOADING
        val resource = ArrayList<Any>(0)

        resource.add(1)

        val status = getCachedResourceStatus(resource, callStatus)

        Assert.assertEquals(status, CallStatus.DONE)
    }

}