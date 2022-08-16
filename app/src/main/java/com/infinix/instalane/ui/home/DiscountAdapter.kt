package com.infinix.instalane.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.databinding.ItemDiscountBinding
import com.infinix.instalane.utils.inflate

class DiscountAdapter(val list : List<Coupon>) : RecyclerView.Adapter<DiscountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_discount)) {

        val binding = ItemDiscountBinding.bind(itemView)

        fun bind(data: Coupon) {
            binding.mPhoto.clipToOutline = true
            Glide.with(itemView.context).load(data.icon).into(binding.mPhoto)
            binding.mDiscount.text = "${data.discount?.toInt()}% off"
            //binding.mTitle.text = data.name
            binding.mExtraView.visibility = View.GONE
            if (adapterPosition == list.size-1)
                binding.mExtraView.visibility = View.VISIBLE
        }
    }
}