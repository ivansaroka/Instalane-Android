package com.infinix.instalane.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.databinding.ItemEmptyBinding
import com.infinix.instalane.utils.inflate

class EmptyAdapter(val text: String) : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(text)

    override fun getItemCount() = 1

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_empty)) {

        val binding = ItemEmptyBinding.bind(itemView)

        fun bind(data: String) {
            binding.mText.text = data
        }
    }
}