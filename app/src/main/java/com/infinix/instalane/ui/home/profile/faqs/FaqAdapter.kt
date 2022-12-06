package com.infinix.instalane.ui.home.profile.faqs

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Faqs
import com.infinix.instalane.databinding.ItemFaqsBinding
import com.infinix.instalane.utils.inflate

class FaqAdapter(val list : List<Faqs>) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_faqs)) {

        val binding = ItemFaqsBinding.bind(itemView)

        fun bind(data: Faqs) {
            binding.mQuestion.text = data.question
            binding.mAnswer.text = data.answer
        }
    }
}