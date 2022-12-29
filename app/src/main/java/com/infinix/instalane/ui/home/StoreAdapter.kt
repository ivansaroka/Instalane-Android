package com.infinix.instalane.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ItemStoreBinding
import com.infinix.instalane.databinding.ItemStoreCompleteBinding
import com.infinix.instalane.utils.inflate

class StoreAdapter(val list : List<Store>, private var isSeeAll :Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var onItemSelected:(Store)->Unit

    companion object {
        const val ITEM_NORMAL = 1
        const val ITEM_COMPLETE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSeeAll) ITEM_COMPLETE else ITEM_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_NORMAL -> ViewHolderNormal(parent)
            ITEM_COMPLETE -> ViewHolderComplete(parent)
            else -> ViewHolderNormal(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderNormal -> holder.bind(list[position])
            is ViewHolderComplete -> holder.bind(list[position])
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolderNormal(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_store)) {

        val binding = ItemStoreBinding.bind(itemView)

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

            binding.mBubble.visibility = View.GONE
            binding.mBestDiscount.visibility = View.GONE
            if (data.bestDiscount!=null && data.bestDiscount!=0){
                binding.mBubble.visibility = View.VISIBLE
                binding.mBestDiscount.visibility = View.VISIBLE
                binding.mBestDiscount.text = "${data.bestDiscount}% off"
            }
            binding.mRate.text = data.calculateRate().toString()
            binding.mDistance.text ="${String.format("%.0f", data.distance)} miles"

            binding.mMembershipRequired.visibility = View.INVISIBLE

            if (data.region?.company?.membershipRequired == true)
                binding.mMembershipRequired.visibility = View.VISIBLE

        }
    }

    inner class ViewHolderComplete(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_store_complete)) {

        val binding = ItemStoreCompleteBinding.bind(itemView)

        fun bind(data: Store) {
            itemView.setOnClickListener { onItemSelected(data) }
            completeInfo(data)
        }

        private fun completeInfo(data: Store){
            binding.mPhotoComplete.clipToOutline = true
            Glide.with(itemView.context).load(data.picture).into(binding.mPhotoComplete)
            binding.mTitleComplete.text = data.name

            binding.mBubbleComplete.visibility = View.GONE
            binding.mBestDiscountComplete.visibility = View.GONE
            if (data.bestDiscount!=null){
                binding.mBubbleComplete.visibility = View.VISIBLE
                binding.mBestDiscountComplete.visibility = View.VISIBLE
                binding.mBestDiscountComplete.text = "${data.bestDiscount}% off"
            }
            binding.mRateComplete.text = data.calculateRate().toString()
            binding.mDistanceComplete.text =  "${String.format("%.0f", data.distance)} miles"

            binding.mMembershipRequired.visibility = View.INVISIBLE
            if (data.region?.company?.membershipRequired == true)
                binding.mMembershipRequired.visibility = View.VISIBLE
        }
    }
}