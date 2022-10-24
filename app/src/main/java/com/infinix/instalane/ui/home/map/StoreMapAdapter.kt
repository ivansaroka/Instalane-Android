package com.infinix.instalane.ui.home.map

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ItemStoreMapBinding
import com.infinix.instalane.utils.inflate

class StoreMapAdapter(val list : List<Store>) : RecyclerView.Adapter<StoreMapAdapter.ViewHolder>() {

    lateinit var onItemSelected:(Store)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_store_map)) {

        val binding = ItemStoreMapBinding.bind(itemView)

        fun bind(data: Store) {
            binding.mPhoto.clipToOutline = true
            itemView.setOnClickListener { onItemSelected(data) }
            binding.mExtraView.visibility = View.GONE
            if (adapterPosition == list.size-1)
                binding.mExtraView.visibility = View.VISIBLE

            completeInfo(data)
        }

        private fun completeInfo(data: Store){
            binding.mPhoto.clipToOutline = true
            Glide.with(itemView.context).load(data.picture).into(binding.mPhoto)
            binding.mTitle.text = data.name
            binding.mDistance.text ="${String.format("%.0f", data.distance)} miles"
            binding.mAddress.text = data.address

            binding.mBubble.visibility = View.GONE
            binding.mBestDiscount.visibility = View.GONE
            if (data.bestDiscount!=null){
                binding.mBubble.visibility = View.VISIBLE
                binding.mBestDiscount.visibility = View.VISIBLE
                binding.mBestDiscount.text = "${data.bestDiscount}% off"
            }
            binding.mRate.text = data.calculateRate().toString()
        }
    }
}