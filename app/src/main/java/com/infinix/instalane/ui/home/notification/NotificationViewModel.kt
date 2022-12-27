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

class NotificationViewModel(application: Application) : BaseViewModel(application) {

    val notificationLiveData = MutableLiveData<List<Notification>>()
    var PAGE = 1

    fun getNotifications() =
        viewModelScope.launch {

            val accessToken = AppPreferences.getUser()!!.accessToken!!
            ApiClient.service::getNotifications.callApi(accessToken, PAGE).collect {
                if (it.isSuccess){
                    PAGE++
                    it.getOrNull()?.let { list-> notificationLiveData.postValue(list.sortedByDescending { it.date })}
                }
                else onError.postValue(it.exceptionOrNull())
            }
        }
}