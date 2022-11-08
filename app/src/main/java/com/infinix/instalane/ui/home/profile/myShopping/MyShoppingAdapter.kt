package com.infinix.instalane.ui.home.profile.myShopping

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.databinding.ItemMyShoppingBinding
import com.infinix.instalane.utils.DateUtils
import com.infinix.instalane.utils.inflate

class MyShoppingAdapter(val list : List<PurchaseHistory>) : RecyclerView.Adapter<MyShoppingAdapter.ViewHolder>() {

    lateinit var onItemSelected:(PurchaseHistory)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_my_shopping)) {

        val binding = ItemMyShoppingBinding.bind(itemView)

        fun bind(data: PurchaseHistory) {
            itemView.setOnClickListener { onItemSelected(data) }
            binding.mName.text = data.store!!.name
            binding.mAmount.text = "$${data.amount!!}"

            binding.mDate.text = ""
                if (data.date!=null){
                val sDate = DateUtils().convertDate(data.date!!, DateUtils.DATE_DEFAULT_API, DateUtils.FORMAT_SHOPPING)
                binding.mDate.text = sDate
            }

        }
    }
}