package com.infinix.instalane.ui.home.notification

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Notification
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class NotificationViewModel(application: Application) : BaseViewModel(application) {

    val notificationLiveData = MutableLiveData<List<Notification>>()
    var PAGE = 1

    fun getNotifications() =
        viewModelScope.launch {
            /*
            val list = ArrayList<Notification>()
            for (i in 0.. 5)
                list.add(Notification())
            notificationLiveData.postValue(list)

             */

            val accessToken = AppPreferences.getUser()!!.accessToken!!
            ApiClient.service::getNotifications.callApi(accessToken, PAGE).collect {
                if (it.isSuccess) it.getOrNull()?.let {notificationLiveData.postValue(it)}
                else onError.postValue(it.exceptionOrNull())
            }
        }
}