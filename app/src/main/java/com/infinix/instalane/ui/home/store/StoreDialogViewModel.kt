package com.infinix.instalane.ui.home.store

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Review
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.flow.collect

class StoreDialogViewModel(application: Application) : BaseViewModel(application) {

    val couponLiveData = MutableLiveData<List<Coupon>>()
    val reviewLiveData = MutableLiveData<List<Review>>()
    val recommendationLiveData = MutableLiveData<List<Store>>()
    val nearLiveData = MutableLiveData<List<Store>>()

    private var PAGE = 1

    fun getDiscount(store: Store) =
        viewModelScope.launch {
            ApiClient.service::getCoupons.callApi(AppPreferences.getUser()!!.accessToken!!, store.id, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores ->
                        couponLiveData.postValue(stores.filter { it.firstPurchase != true })
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun getReview(store: Store) =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            ApiClient.service::getStoreReviews.callApi(accessToken, store.id!!, PAGE).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { reviews -> reviewLiveData.postValue(reviews) }
                else
                    onError.postValue(it.exceptionOrNull())
            }

            /*
            val list = ArrayList<Review>()
            for (i in 0.. 5){ list.add(Review()) }
            reviewLiveData.postValue(list)
             */

        }

    fun getNearStores() =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            val lat = null//SingletonLocation.instance.getLocation()!!.latitude
            val long = null//SingletonLocation.instance.getLocation()!!.longitude
            val radius = null//ConstantValue.RADIUS

            ApiClient.service::getStores.callApi(accessToken,lat, long,radius, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores ->
                        stores.forEach { it.calculateDistance(SingletonLocation.instance.myLocation!!) }
                        nearLiveData.postValue(stores)
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
            val radius = null//ConstantValue.RADIUS

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
}