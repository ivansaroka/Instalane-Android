package com.infinix.instalane.ui.homeGuard.purchaseSummary

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.databinding.ItemProductGuardBinding
import com.infinix.instalane.utils.inflate

class ProductGuardAdapter(val list : List<Order.ItemOrder>, val onSelectedItems:(ArrayList<Order.ItemOrder>)->Unit) : RecyclerView.Adapter<ProductGuardAdapter.ViewHolder>() {

    val listSelected = ArrayList<Order.ItemOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_product_guard)) {

        val binding = ItemProductGuardBinding.bind(itemView)

        fun bind(data: Order.ItemOrder) {

            Glide.with(itemView.context).load(data.product?.picture).into( binding.mPhoto)
            binding.mName.text = data.product?.name

            binding.mCheck.setImageResource(R.drawable.bg_circle_gray)
            if (binding.mContAll.tag == "ON")
                binding.mCheck.setImageResource(R.drawable.ic_check)
            else
                binding.mContAll.tag = "OFF"

            binding.mContAll.setOnClickListener {
                if (binding.mContAll.tag == "OFF"){
                    binding.mContAll.tag = "ON"
                    binding.mCheck.setImageResource(R.drawable.ic_check)
                    listSelected.add(data)
                }else{
                    binding.mContAll.tag = "OFF"
                    binding.mCheck.setImageResource(R.drawable.bg_circle_gray)
                    listSelected.remove(data)
                }
                onSelectedItems.invoke(listSelected)
            }
        }
    }
}