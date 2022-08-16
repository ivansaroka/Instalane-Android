package com.infinix.instalane.ui.home.profile.paymentMethods

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Card
import com.infinix.instalane.databinding.ItemPaymentMethodBinding
import com.infinix.instalane.utils.inflate

class PaymentMethodAdapter(val list : List<Card>) : RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun getSelectedCard() : Card{
        if (selectedPosition<0) return list[0]
        return list[selectedPosition]
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_payment_method)) {

        val binding = ItemPaymentMethodBinding.bind(itemView)

        fun bind(data: Card) {
            binding.mContAll.setBackgroundResource(R.drawable.bg_box_border)
            binding.mButtonRadio.setImageResource(R.drawable.ic_radio_btn_off)
            if (selectedPosition == adapterPosition){
                binding.mContAll.setBackgroundResource(R.drawable.bg_box_border_orange)
                binding.mButtonRadio.setImageResource(R.drawable.ic_radio_btn_on)
            }

            binding.mContAll.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}