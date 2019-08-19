package com.afterapps.heimdall.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterapps.heimdall.R
import com.afterapps.heimdall.domain.Collection
import com.afterapps.heimdall.domain.Image
import com.afterapps.heimdall.network.CallStatus
import com.afterapps.heimdall.ui.collections.CollectionsAdapter
import com.afterapps.heimdall.ui.images.ImagesAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("collections")
fun bindRecyclerViewCollections(recyclerView: RecyclerView, collections: List<Collection>?) {
    collections?.let {
        val adapter = recyclerView.adapter as CollectionsAdapter
        adapter.submitList(collections)
    }
}

@BindingAdapter("images")
fun bindRecyclerViewImages(recyclerView: RecyclerView, images: List<Image>?) {
    images?.let {
        val adapter = recyclerView.adapter as ImagesAdapter
        adapter.submitList(images)
    }
}

@BindingAdapter("collectionCoverUrl")
fun bindImageViewCollection(imageView: ImageView, srcUrl: String?) {
    srcUrl?.let {
        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_collection_placeholder)
            .error(R.drawable.ic_collection_error)

        Glide.with(imageView.context)
            .load(srcUrl)
            .apply(requestOptions)
            .into(imageView)
    }
}

@BindingAdapter("imagesThumbnailUrl")
fun bindImageViewImageThumbnail(imageView: ImageView, thumbnailUrl: String?) {
    thumbnailUrl?.let {
        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_error)

        Glide.with(imageView.context)
            .load(thumbnailUrl)
            .apply(requestOptions)
            .into(imageView)
    }
}

@BindingAdapter("callStatus")
fun bindImageViewCallStatus(statusImageView: ImageView, status: CallStatus?) {
    when (status) {
        CallStatus.LOADING -> {
            statusImageView.visibility = View.GONE
        }
        CallStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_loading_error)
        }
        CallStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("callStatus")
fun bindProgressBarCallStatus(progressBar: View, status: CallStatus?) {
    when (status) {
        CallStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        CallStatus.ERROR -> {
            progressBar.visibility = View.GONE
        }
        CallStatus.DONE -> {
            progressBar.visibility = View.GONE
        }
    }
}