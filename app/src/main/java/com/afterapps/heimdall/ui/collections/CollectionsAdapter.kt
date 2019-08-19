package com.afterapps.heimdall.ui.collections

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterapps.heimdall.databinding.ItemCollectionBinding
import com.afterapps.heimdall.domain.Collection

class CollectionsAdapter(private val collectionListener: CollectionListener) :
    ListAdapter<Collection, CollectionsAdapter.CollectionViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = getItem(position)
        holder.bind(collection, collectionListener)
    }

    class CollectionViewHolder(private var binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(collection: Collection, collectionListener: CollectionListener) {
            binding.collection = collection
            binding.collectionListener = collectionListener
            binding.collectionImageView = binding.itemCollectionImageView
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Collection>() {
        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem == newItem
        }
    }
}

class CollectionListener(private val onClickListener: (collection: Collection, collectionImageView: ImageView) -> Unit) {
    fun onClick(collection: Collection, collectionImageView: ImageView) {
        onClickListener(collection, collectionImageView)
    }
}