package com.afterapps.heimdall.ui.images

import com.afterapps.heimdall.domain.Image
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ImagesAdapterTest {

    private val image = Image(
        "1",
        imageUrl = "ImageUrl1",
        thumbnailUrl = "ThumbnailUrl1",
        collectionId = "1"
    )

    /** GIVEN - a different [Image] with the same [Image.id] */
    private val otherImage = Image(
        "1",
        imageUrl = "ImageUrl2",
        thumbnailUrl = "ThumbnailUrl2",
        collectionId = "2"
    )

    @Test
    fun areItemsTheSame_differentImage() {
        /** WHEN - [otherImage] gets added to [ImagesAdapter] */
        val result = ImagesAdapter.areItemsTheSame(image, otherImage)

        /** THEN - [ImagesAdapter.areContentsTheSame] returns true */
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun areContentTheSame_differentImage() {
        /** WHEN - [otherImage] gets added to [ImagesAdapter] */
        val result = ImagesAdapter.areContentsTheSame(image, otherImage)

        /** THEN - [ImagesAdapter.areContentsTheSame] returns false */
        assertThat(result).isEqualTo(false)
    }
}