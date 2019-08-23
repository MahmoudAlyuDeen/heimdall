package com.afterapps.heimdall.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afterapps.heimdall.databinding.ItemResultBinding
import com.afterapps.heimdall.domain.Result

class ResultsAdapter(private val resultListener: ResultListener) :
    ListAdapter<Result, ResultsAdapter.ResultViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result, resultListener)
    }

    class ResultViewHolder(private var binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result, resultListener: ResultListener) {
            binding.result = result
            binding.resultListener = resultListener
            binding.executePendingBindings()
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