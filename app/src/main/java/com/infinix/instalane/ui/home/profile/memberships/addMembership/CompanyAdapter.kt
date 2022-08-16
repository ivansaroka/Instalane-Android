package com.infinix.instalane.ui.home.profile.memberships.addMembership

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.databinding.ItemMembershipBinding
import com.infinix.instalane.utils.inflate

class CompanyAdapter(val list : List<Memberships.Company>) : RecyclerView.Adapter<CompanyAdapter.ViewHolder>() {

    lateinit var onItemSelected : (Memberships.Company)-> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_membership)) {

        val binding = ItemMembershipBinding.bind(itemView)

        fun bind(data: Memberships.Company) {
            Glide.with(itemView.context).load(data.picture).placeholder(R.drawable.ic_photo_placeholder).into(binding.mPhoto)
            binding.mTitle.text = data.name

            itemView.setOnClickListener { onItemSelected.invoke(data) }
        }
    }
}