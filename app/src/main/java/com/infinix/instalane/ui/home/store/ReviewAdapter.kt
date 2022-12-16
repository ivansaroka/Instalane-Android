package com.infinix.instalane.ui.home.store

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Review
import com.infinix.instalane.databinding.ItemReviewBinding
import com.infinix.instalane.utils.inflate

class ReviewAdapter(val list : ArrayList<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun addPage(newPageList : List<Review>){
        list.addAll(newPageList)
        notifyItemRangeInserted(list.size - newPageList.size, list.size)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_review)) {

        val binding = ItemReviewBinding.bind(itemView)

        fun bind(data: Review) {
            binding.mTitle.text = data.userName
            binding.mComment.text = data.comment
            binding.mRateBar.rating = data.rating!!.toFloat()
            binding.mFace.setImageResource(
                when(data.rating){
                1-> R.drawable.rate1
                    2-> R.drawable.rate2
                    3-> R.drawable.rate3
                    4-> R.drawable.rate4
                    5-> R.drawable.rate5
                    else -> R.drawable.rate4
            })
        }
    }
}