package com.afterapps.heimdall.ui.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterapps.heimdall.databinding.ItemImageBinding
import com.afterapps.heimdall.domain.Image

class ImagesAdapter(private val imageListener: ImageListener) :
    ListAdapter<Image, ImagesAdapter.ImageViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image, imageListener)
    }

    class ImageViewHolder(private var binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image, imageListener: ImageListener) {
            binding.image = image
            binding.imageListener = imageListener
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }
}

class ImageListener(private val onClickListener: (image: Image) -> Unit) {

    fun onClick(image: Image) {
        onClickListener(image)
    }
}