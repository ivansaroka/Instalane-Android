package com.infinix.instalane.ui.start.tutorial

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.local.TutorialEntity
import com.infinix.instalane.databinding.ItemTutorialBinding
import com.infinix.instalane.utils.inflate

class TutorialAdapter(val list : List<TutorialEntity>) : RecyclerView.Adapter<TutorialAdapter.ViewHolder>() {

    lateinit var onCloseTutorial:()->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_tutorial)) {

        val binding = ItemTutorialBinding.bind(itemView)

        fun bind(data: TutorialEntity) {
            binding.mPhoto.setImageResource(data.drawableID!!)

            binding.mClose.visibility = View.GONE
            if (adapterPosition == list.size-1)
                binding.mClose.visibility = View.VISIBLE

            binding.mClose.setOnClickListener { onCloseTutorial.invoke() }
        }
    }
}