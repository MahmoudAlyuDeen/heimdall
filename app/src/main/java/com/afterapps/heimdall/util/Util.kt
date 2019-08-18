package com.afterapps.heimdall.util

fun getImageUrl(imageUrlFormat: String, imageId: String): String {
    return imageUrlFormat.format(imageId)
}
