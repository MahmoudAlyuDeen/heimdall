package com.afterapps.heimdall.util

import android.graphics.drawable.Drawable
import com.afterapps.heimdall.network.CallStatus
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/** Returns formatted URL after replacing placeholder with ID */
fun getImageUrl(imageUrlFormat: String, imageId: String): String {
    return imageUrlFormat.format(imageId)
}

/** Returns CallStatus.DONE if collections isn't empty and returns the current status otherwise */
fun getCallStatus(collections: List<Any>, currentStatus: CallStatus?): CallStatus? {
    return if (collections.isNullOrEmpty()) currentStatus else CallStatus.DONE
}

/** Executes onDone() on both success and failure */
class DrawableRequestListener(private val onDone: () -> Unit) : RequestListener<Drawable> {
    override fun onResourceReady(r: Drawable?, a: Any?, t: Target<Drawable>?, d: DataSource?, i: Boolean) = run {
        onDone()
        false
    }

    override fun onLoadFailed(e: GlideException?, m: Any?, t: Target<Drawable>?, i: Boolean) = run {
        onDone()
        false
    }
}