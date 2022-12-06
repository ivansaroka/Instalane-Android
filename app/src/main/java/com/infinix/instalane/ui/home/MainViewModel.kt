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
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {

    val nearStoreLiveData = MutableLiveData<List<Store>>()
    val recommendationLiveData = MutableLiveData<List<Store>>()
    val couponLiveData = MutableLiveData<List<Coupon>>()

    fun getVisitedStore() =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            val lat = null//SingletonLocation.instance.getLocation()!!.latitude
            val long = null//SingletonLocation.instance.getLocation()!!.longitude
            val radius = null//ConstantValue.RADIUS

            ApiClient.service::getStores.callApi(accessToken,lat, long, radius, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores ->
                        stores.forEach { it.calculateDistance(SingletonLocation.instance.myLocation!!) }
                        nearStoreLiveData.postValue(stores)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun getRecommendedStores() =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            val lat = null//SingletonLocation.instance.getLocation()!!.latitude
            val long = null//SingletonLocation.instance.getLocation()!!.longitude
            val radius = null//ConstantValue.RADIUS * 0.000621371f

            ApiClient.service::getStores.callApi(accessToken,lat, long,radius, null).collect {
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
                    it.getOrNull()?.let { stores -> couponLiveData.postValue(stores) }
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
}