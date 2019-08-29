package com.afterapps.heimdall.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.afterapps.heimdall.network.CallStatus

/** Returns formatted URL after replacing placeholder with ID */
fun getImageUrl(imageUrlFormat: String, imageId: String): String {
    return imageUrlFormat.format(imageId)
}

/** Returns CallStatus.DONE if cachedResource isn't empty and returns the current status otherwise */
fun getCachedResourceStatus(cachedResource: List<Any>?, currentStatus: CallStatus?): CallStatus? {
    return if (cachedResource.isNullOrEmpty()) currentStatus else CallStatus.DONE
}

fun hideKeyboard(activity: Activity?) {
    val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = activity.currentFocus
    if (focusedView != null) {
        inputManager.hideSoftInputFromWindow(focusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}
