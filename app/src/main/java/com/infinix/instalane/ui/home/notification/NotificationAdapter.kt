package com.infinix.instalane.ui.home.notification

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Notification
import com.infinix.instalane.databinding.ItemNotificationBinding
import com.infinix.instalane.utils.DateUtils
import com.infinix.instalane.utils.inflate
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(val list : ArrayList<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    var onDeleteNotification : ((Notification, Int) -> Unit?)? =null

    fun addPage(newPageList : List<Notification>){
        list.addAll(newPageList)
        notifyItemRangeInserted(list.size - newPageList.size, list.size)
    }

    fun removeNotification(pos:Int){
        list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_notification)) {

        val binding = ItemNotificationBinding.bind(itemView)

        fun bind(data: Notification) {
            binding.mName.text = data.title
            binding.mDescription.text = data.message
            binding.mDate.text = "Unknown"
            if (!data.date.isNullOrEmpty()) {
                val date = DateUtils().convertFromStringToDate(data.date!!, DateUtils.FORMAT_NOTIFICATION_API)
                if (date != null) {

                    val timeZone: String = Calendar.getInstance().timeZone.id
                    val local = Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))

                    val sDate =  DateUtils().calculateDateDifference(local)
                    binding.mDate.text = sDate.replace("a. m.", "AM").replace("p. m.", "PM")
                }
            }

            binding.mContButtons.setOnClickListener {
                binding.swipeLayout.close()
                onDeleteNotification?.invoke(data, absoluteAdapterPosition)
            }
        }
    }
}