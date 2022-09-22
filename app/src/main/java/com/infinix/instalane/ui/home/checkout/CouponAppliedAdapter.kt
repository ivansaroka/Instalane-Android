package com.infinix.instalane.ui.home.checkout

import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Notification
import com.infinix.instalane.databinding.ItemCouponAppliedBinding
import com.infinix.instalane.databinding.ItemNotificationBinding
import com.infinix.instalane.utils.inflate

class CouponAppliedAdapter(val list : ArrayList<Coupon>) : RecyclerView.Adapter<CouponAppliedAdapter.ViewHolder>() {

    lateinit var onRemoveAll:()->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun getTotalDiscount(subtotal:Float) : Float {
        var totalDiscount = 0f
        list.forEach { totalDiscount += it.discount!! }
        if (totalDiscount!=0f)
            return (totalDiscount*subtotal)/100
        return totalDiscount
    }

    fun getCouponIdList() : List<String> {
        return list.filter { it.id != null }.map { it.id!! }
    }

    fun addCoupon(coupon: Coupon, context: Context){
        val couponFound = list.find { it.id == coupon.id }
        if (couponFound==null){
            list.add(coupon)
            notifyItemInserted(list.size-1)
        }else{
            Toast.makeText(context, "This coupon was already added", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_coupon_applied)) {

        val binding = ItemCouponAppliedBinding.bind(itemView)

        fun bind(data: Coupon) {

            binding.mDiscount.text = "INSTA${data.discount?.toInt()}%OFF"
            binding.mRemove.setOnClickListener {
                list.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                if (list.isEmpty()) onRemoveAll.invoke()
            }
        }
    }
}