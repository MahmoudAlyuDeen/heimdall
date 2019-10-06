package com.afterapps.heimdall.ui.collections

import com.afterapps.heimdall.domain.Collection
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CollectionsAdapterTest {

    private val collection = Collection(
        "1",
        name = "Name1",
        coverUrl = "CoverUrl1",
        shareUrl = "ShareUrl1",
        totalItemCount = 1,
        createdTime = "CreatedTime1",
        updatedTime = "UpdatedTime1"
    )

    /** GIVEN - a different [Collection] with the same [Collection.id] */
    private val otherCollection = Collection(
        "1",
        name = "Name2",
        coverUrl = "CoverUrl2",
        shareUrl = "ShareUrl2",
        totalItemCount = 2,
        createdTime = "CreatedTime2",
        updatedTime = "UpdatedTime2"
    )

    @Test
    fun areItemsTheSame_differentCollection() {
        /** WHEN - [otherCollection] gets added to [CollectionsAdapter] */
        val result = CollectionsAdapter.areItemsTheSame(collection, otherCollection)

        /** THEN - [CollectionsAdapter.areContentsTheSame] returns true */
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun areContentTheSame_differentCollection() {
        /** WHEN - [otherCollection] gets added to [CollectionsAdapter] */
        val result = CollectionsAdapter.areContentsTheSame(collection, otherCollection)

        /** THEN - [CollectionsAdapter.areContentsTheSame] returns false */
        assertThat(result).isEqualTo(false)
    }
}