package com.afterapps.heimdall.util

import android.graphics.drawable.Drawable
import android.transition.Transition
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/** Listener for SearchView onQueryTextSubmit */
class OnQueryTextListener(private val onTextSubmit: (query: String?) -> Unit) :
    SearchView.OnQueryTextListener {
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

/** Glide DrawableRequestListener, executes onDone() on both success and failure */
class DrawableRequestListener(private val onDone: () -> Unit) :
    RequestListener<Drawable> {
    override fun onResourceReady(r: Drawable?, a: Any?, t: Target<Drawable>?, d: DataSource?, i: Boolean) = run {
        onDone()
        false
    }

    override fun onLoadFailed(e: GlideException?, m: Any?, t: Target<Drawable>?, i: Boolean) = run {
        onDone()
        false
    }
}

/** RecyclerView OnScrollListener with onScrollListener */
class OnScrollListener(private val onScroll: () -> Unit) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) onScroll()
        super.onScrolled(recyclerView, dx, dy)
    }
}

class TransitionListener(private val onTransitionEnd: () -> Unit) : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition) {
        onTransitionEnd()
    }

    // unused callbacks

    override fun onTransitionResume(transition: Transition) = Unit
    override fun onTransitionPause(transition: Transition) = Unit
    override fun onTransitionCancel(transition: Transition) = Unit
    override fun onTransitionStart(transition: Transition) = Unit
}