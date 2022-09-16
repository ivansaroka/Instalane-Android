package com.infinix.instalane.ui.home.barcode

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ItemProductBinding
import com.infinix.instalane.utils.inflate
import com.zerobranch.layout.SwipeLayout

class ProductAdapter(val list : ArrayList<Product>, val store: Store?=null, val isCheckout:Boolean? = false, val isBasket:Boolean? = false) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    lateinit var onRemove:(Product)->Unit
    lateinit var onAddCoupon:(Product)->Unit
    lateinit var onRemoveCoupon:()->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun addProduct(product: Product){
        list.add(product)
        notifyItemInserted(list.size-1)
    }

    fun removeProduct(product: Product){
        list.forEachIndexed { index, prod ->
            if (prod.id == product.id){
                list.removeAt(index)
                notifyItemRemoved(index)
                return
            }
        }
    }

    fun addCoupon(coupon: Coupon, product: Product){
        list.forEachIndexed { index, prod ->
            if (prod.id == product.id) {
                prod.couponApplied = coupon
                notifyItemChanged(index)
                return
            }
        }
    }

    fun calculateDiscount() : Float {
        var total = 0f

        list.forEach {
            if (it.couponApplied!=null){
                total += (it.getPriceByRegion(store?.region) * it.couponApplied!!.discount!!)/100
            }
        }

        return total
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_product)) {

        val binding = ItemProductBinding.bind(itemView)

        fun bind(data: Product) {

            Glide.with(itemView.context).load(data.picture).into( binding.mPhoto)
            binding.mName.text = data.name
            binding.mPrice.text = "$${data.getPriceByRegion(store?.region)}"

            binding.mAddCoupon.setOnClickListener { onAddCoupon.invoke(data) }
            binding.mAddCoupon.visibility = View.GONE
            binding.mCouponApplied.visibility = View.GONE
            binding.mRemoveCoupon.visibility = View.GONE
            binding.mLineDivider.visibility = View.GONE
            if(isCheckout!=null && isCheckout) {
                binding.mLineDivider.visibility = View.VISIBLE
                binding.mAddCoupon.visibility = View.VISIBLE
                if (data.couponApplied != null) {
                    binding.mAddCoupon.visibility = View.INVISIBLE
                    binding.mCouponApplied.visibility = View.VISIBLE
                    binding.mRemoveCoupon.visibility = View.VISIBLE
                    binding.mCouponApplied.text = "INSTA${data.couponApplied!!.discount?.toInt()}%OFF"
                } else {
                    if (data.hasCoupons())
                        binding.mAddCoupon.visibility = View.VISIBLE
                    else
                        binding.mAddCoupon.visibility = View.GONE
                }
            }

            binding.mRemoveCoupon.setOnClickListener {
                data.couponApplied = null
                notifyItemChanged(adapterPosition)
                onRemoveCoupon.invoke()
            }

            binding.mContRemove.visibility = View.GONE
            binding.swipeLayout.isEnabledSwipe = false
            binding.swipeLayout.currentDirection = -1
            if (isBasket!=null && isBasket) {
                binding.mContRemove.visibility = View.VISIBLE
                binding.swipeLayout.isEnabledSwipe = true
                binding.swipeLayout.currentDirection = SwipeLayout.LEFT
            }

            binding.mRemove.setOnClickListener {
                binding.swipeLayout.close()
                onRemove.invoke(data)
            }
            binding.mContButtons.setOnClickListener {
                binding.swipeLayout.close()
                onRemove.invoke(data)
            }

        }
    }
}