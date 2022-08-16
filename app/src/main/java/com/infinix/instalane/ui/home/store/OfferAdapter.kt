package com.infinix.instalane.ui.home.store

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.databinding.ItemOfferBinding
import com.infinix.instalane.utils.DateUtils
import com.infinix.instalane.utils.inflate

class OfferAdapter(val list : List<Coupon>) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_offer)) {

        val binding = ItemOfferBinding.bind(itemView)

        fun bind(data: Coupon) {
            binding.mPhoto.clipToOutline = true
            Glide.with(itemView.context).load(data.icon).into(binding.mPhoto)
            binding.mTitle.text = "${data.discount?.toInt()}% off"

            if (!data.finishTime.isNullOrEmpty()){
                val sExpire = DateUtils().convertDate(data.finishTime, DateUtils.DATE_DEFAULT_API, DateUtils.FORMAT_EXPIRED)
                binding.mExpire.text = "Expired $sExpire"
            }

        }
    }
}