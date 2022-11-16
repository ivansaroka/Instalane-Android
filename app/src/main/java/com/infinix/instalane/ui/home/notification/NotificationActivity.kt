package com.infinix.instalane.ui.home.notification

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.notification))
        viewModel.getNotifications()
    }

    private fun showData(list : List<Notification>){
        if (list.isEmpty())
            binding.mList.adapter = EmptyAdapter(getString(R.string.empty_notification))
        else
            binding.mList.adapter = NotificationAdapter(list)
    }
}