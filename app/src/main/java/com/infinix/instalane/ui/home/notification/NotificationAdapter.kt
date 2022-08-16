package com.infinix.instalane.ui.home.notification

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Notification
import com.infinix.instalane.databinding.ItemNotificationBinding
import com.infinix.instalane.utils.inflate

class NotificationAdapter(val list : List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_notification)) {

        val binding = ItemNotificationBinding.bind(itemView)

        fun bind(data: Notification) {

        }
    }
}