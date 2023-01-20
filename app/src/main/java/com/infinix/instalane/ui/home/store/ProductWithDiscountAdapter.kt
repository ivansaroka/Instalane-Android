package com.infinix.instalane.ui.home.store

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ItemProductDiscountBinding
import com.infinix.instalane.utils.inflate

class ProductWithDiscountAdapter(val list : List<Product>, val store: Store?=null) : RecyclerView.Adapter<ProductWithDiscountAdapter.ViewHolder>() {

    lateinit var onProductSelected:(Product)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_product_discount)) {

        val binding = ItemProductDiscountBinding.bind(itemView)

        fun bind(data: Product) {
            Glide.with(itemView.context).load(data.picture).into( binding.mPhoto)
            binding.mName.text = data.name
            binding.mPrice.text = "$${data.getPriceByRegion(store?.region)}"
        }
    }
}