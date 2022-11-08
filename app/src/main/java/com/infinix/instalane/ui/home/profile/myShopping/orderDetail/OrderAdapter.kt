package com.infinix.instalane.ui.home.profile.myShopping.orderDetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ItemOrderBinding
import com.infinix.instalane.utils.inflate

class OrderAdapter(val list : List<Order.ItemOrder>, val store: Store?=null) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_order)) {

        val binding = ItemOrderBinding.bind(itemView)

        fun bind(data: Order.ItemOrder) {

            binding.mPrice.text = "$${data.price}"
            data.product?.let {
                binding.mName.text = it.name
                //binding.mPrice.text = "$${it.getPriceByRegion(store?.region)}"
                Glide.with(itemView.context).load(it.picture).into(binding.mPhoto)
            }
        }
    }
}