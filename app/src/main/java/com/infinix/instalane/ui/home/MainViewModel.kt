package com.infinix.instalane.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.DeviceTokenRequest
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import com.infinix.instalane.utils.ConstantValue
import com.infinix.instalane.utils.DateUtils
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {

    val recommendationLiveData = MutableLiveData<List<Store>>()
    val couponLiveData = MutableLiveData<List<Coupon>>()
    val notificationLiveData = MutableLiveData<Int>()
    var radius = 0
    
    companion object{
        const val EMPTY_NOTIFICATION = 0
        const val ALL_READ = 1
        const val UNREAD_NOTIFICATION = 2
    }


    fun getRecommendedStores() =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            val lat = SingletonLocation.instance.getLocation()!!.latitude
            val long = SingletonLocation.instance.getLocation()!!.longitude

            radius +=  if (radius == 0) ConstantValue.RADIUS else 100

            Log.i("RADIUSSEARCH", "radius : $radius")

            ApiClient.service::getStores.callApi(accessToken,lat, long, radius.toFloat(), null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores ->
                        stores.forEach { it.calculateDistance(SingletonLocation.instance.myLocation!!) }
                        recommendationLiveData.postValue(stores)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun getDiscounts() =
        viewModelScope.launch {
            ApiClient.service::getCoupons.callApi(AppPreferences.getUser()!!.accessToken!!, null, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores -> couponLiveData.postValue(stores.filter {!it.code.isNullOrEmpty() }) }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun sendDeviceToken() {
        if (AppPreferences.getDeviceToken().isNullOrEmpty())
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.i("FIREBASEMESSAGE", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.i("FIREBASEMESSAGE", "token: $token")

                val request = DeviceTokenRequest(
                    accessToken = AppPreferences.getUser()!!.accessToken,
                    deviceToken = token
                )

                viewModelScope.launch {
                    AppPreferences.setDeviceToken(token)
                    ApiClient.service::registerDeviceToken.callApi(request).collect {}
                }
            })
    }

    fun getNotifications() =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            ApiClient.service::getNotifications.callApi(accessToken, 1).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let { list ->

                        if (list.isEmpty()){
                            notificationLiveData.postValue(EMPTY_NOTIFICATION)
                            return@let
                        }

                        val lastDate = AppPreferences.getLastDateNotification()
                        if (lastDate != 0L){
                            var contUnread = 0
                            list.forEach { notification ->
                                if (!notification.date.isNullOrEmpty()) {
                                   val date =  DateUtils().convertFromStringToDate(notification.date!!, DateUtils.FORMAT_NOTIFICATION_API)
                                    if (date?.time!! > lastDate)
                                        contUnread++
                                }
                            }
                            if (contUnread==0)
                                notificationLiveData.postValue(ALL_READ)
                            else
                                notificationLiveData.postValue(UNREAD_NOTIFICATION)
                        } else {
                            AppPreferences.setLastDateNotification()
                            notificationLiveData.postValue(ALL_READ)
                        }
                    }
                }
                else onError.postValue(it.exceptionOrNull())
            }
        }
}