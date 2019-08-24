package com.afterapps.heimdall.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afterapps.heimdall.R
import com.afterapps.heimdall.databinding.ItemResultBinding
import com.afterapps.heimdall.domain.Result

private const val ITEM_VIEW_TYPE_PROGRESS = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class ResultsAdapter(private val resultListener: ResultListener) :
    PagedListAdapter<Result, RecyclerView.ViewHolder>(DiffCallBack) {

    /** Show a ProgressView footer when true */
    var adding: Boolean = false

    override fun getItemViewType(position: Int): Int =
        if (adding && position == itemCount - 1) ITEM_VIEW_TYPE_PROGRESS else ITEM_VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> ResultViewHolder.from(parent)
            ITEM_VIEW_TYPE_PROGRESS -> ProgressViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ResultViewHolder -> {
                val result = getItem(position)
                result?.let { holder.bind(it, resultListener) }
            }
        }
    }

    class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): ProgressViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_progress, parent, false)
                return ProgressViewHolder(view)
            }
        }
    }

    class ResultViewHolder(private var binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result, resultListener: ResultListener) {
            binding.result = result
            binding.resultListener = resultListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ResultViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return ResultViewHolder(ItemResultBinding.inflate(inflater, parent, false))
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }
}

class ResultListener(private val onClickListener: (result: Result) -> Unit) {
    fun onClick(result: Result) {
        onClickListener(result)
    }
}