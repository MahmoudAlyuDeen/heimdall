package com.afterapps.heimdall.util

import com.afterapps.heimdall.network.CallStatus

/** Returns formatted URL after replacing placeholder with ID */
fun getImageUrl(imageUrlFormat: String, imageId: String): String {
    return imageUrlFormat.format(imageId)
}

/** Returns CallStatus.DONE if collections isn't empty and returns the current status otherwise */
fun getCallStatus(collections: List<Any>, currentStatus: CallStatus?): CallStatus? {
    return if (collections.isNullOrEmpty()) currentStatus else CallStatus.DONE
}

