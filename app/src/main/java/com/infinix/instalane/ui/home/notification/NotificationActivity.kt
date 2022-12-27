package com.infinix.instalane.ui.home.notification

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.Notification
import com.infinix.instalane.databinding.ActivityNotificationBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.base.EmptyAdapter

class NotificationActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[NotificationViewModel::class.java].apply {
            notificationLiveData.observe(this@NotificationActivity, this@NotificationActivity::showData)
            onError.observe(this@NotificationActivity) { hideProgressDialog() }
        }
    }

    private val binding by lazy { ActivityNotificationBinding.inflate(layoutInflater) }
    private var isRefreshing = false
    private var endOfPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.notification))

        binding.mList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastItemVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                val endHasBeenReached = lastItemVisible == totalItemCount-1

                if (totalItemCount >= 10 && endHasBeenReached && !isRefreshing && !endOfPage) {
                    isRefreshing = true
                    binding.mProgressPage.visibility = View.VISIBLE
                    viewModel.getNotifications()
                }
            }
        })

        AppPreferences.setLastDateNotification()
        viewModel.getNotifications()
    }

    private fun showData(list : List<Notification>){
        isRefreshing = false
        binding.mProgressPage.visibility = View.GONE

        if (list.isEmpty())
            binding.mList.adapter = EmptyAdapter(getString(R.string.empty_notification))
        else {
            if (binding.mList.adapter == null)
                binding.mList.adapter = NotificationAdapter(ArrayList(list))
            else {
                (binding.mList.adapter as NotificationAdapter).addPage(list)
                if (list.isEmpty())
                    endOfPage = true
            }
        }

    }
}