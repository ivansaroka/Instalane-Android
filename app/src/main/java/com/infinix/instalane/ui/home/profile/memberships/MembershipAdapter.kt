package com.infinix.instalane.ui.home.profile.memberships

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.databinding.ItemMembershipBinding
import com.infinix.instalane.utils.inflate

class MembershipAdapter(val list : List<Memberships>) : RecyclerView.Adapter<MembershipAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_membership)) {

        val binding = ItemMembershipBinding.bind(itemView)

        fun bind(data: Memberships) {
            Glide.with(itemView.context).load(data.company?.picture).placeholder(R.drawable.ic_photo_placeholder).into(binding.mPhoto)
            binding.mTitle.text = data.company?.name
            binding.mNumber.text = data.membershipNumber?.toString()

            if (data.verified==true)
                binding.mContAll.setBackgroundResource(R.drawable.bg_box_membership_verified)
            else
                binding.mContAll.setBackgroundResource(R.drawable.bg_box_membership_pending)

        }
    }
}