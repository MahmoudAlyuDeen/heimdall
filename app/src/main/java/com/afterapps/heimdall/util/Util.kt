package com.afterapps.heimdall.util

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
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

fun hideKeyboard(activity: Activity?) {
    val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val focusedView = activity.currentFocus
    if (focusedView != null) {
        inputManager.hideSoftInputFromWindow(focusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

/** Glide DrawableRequestListener, executes onDone() on both success and failure */
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

/** Listener for SearchView onQueryTextSubmit */
class OnQueryTextListener(private val onTextSubmit: (query: String?) -> Unit) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?) = run {
        onTextSubmit(query)
        false
    }

    override fun onQueryTextChange(newText: String?) = false
}

/** Listener to respond to SearchView onMenuItemActionCollapse */
class OnActionExpandListener(private val onCollapse: () -> Unit) : MenuItem.OnActionExpandListener {
    override fun onMenuItemActionCollapse(item: MenuItem?) = run {
        onCollapse()
        false
    }

    override fun onMenuItemActionExpand(item: MenuItem?) = true
}

class OnScrollListener(private val onScroll: () -> Unit) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) onScroll()
        super.onScrolled(recyclerView, dx, dy)
    }
}
