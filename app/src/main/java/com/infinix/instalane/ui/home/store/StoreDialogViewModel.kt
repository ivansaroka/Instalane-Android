package com.infinix.instalane.ui.home.store

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.ManyProductsRequest
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Review
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlinx.coroutines.flow.collect
import kotlin.collections.ArrayList

class StoreDialogViewModel(application: Application) : BaseViewModel(application) {

    val couponLiveData = MutableLiveData<List<Coupon>>()
    val productsWithCouponLiveData = MutableLiveData<List<Product>>()
    val reviewLiveData = MutableLiveData<List<Review>>()
    val recommendationLiveData = MutableLiveData<List<Store>>()
    val nearLiveData = MutableLiveData<List<Store>>()

    private var PAGE = 1

    fun getDiscount(store: Store) =
        viewModelScope.launch {
            ApiClient.service::getCoupons.callApi(AppPreferences.getUser()!!.accessToken!!, store.id, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { coupons ->

                        if (coupons.isEmpty()){
                            productsWithCouponLiveData.postValue(ArrayList())
                            return@let
                        }

                        val productList = ArrayList<String>()
                        coupons.forEach { coupon ->
                            if (!coupon.couponProducts.isNullOrEmpty())
                                productList.addAll(coupon.couponProducts)
                        }
                        val request = ManyProductsRequest().apply {
                            accessToken = AppPreferences.getUser()!!.accessToken!!
                            products = productList.distinct()
                        }

                        ApiClient.service::getManyProducts.callApi(request).collect {
                            if (it.isSuccess)
                                it.getOrNull()?.let { products ->
                                    productsWithCouponLiveData.postValue(products)
                                }
                            else
                                onError.postValue(it.exceptionOrNull())
                        }

                        //couponLiveData.postValue(coupons.filter { it.firstPurchase != true && !it.code.isNullOrEmpty() })
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun getReview(store: Store) =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!
            ApiClient.service::getStoreReviews.callApi(accessToken, store.id!!, PAGE).collect {
                if (it.isSuccess){
                    PAGE++
                    it.getOrNull()?.let { reviews -> reviewLiveData.postValue(reviews) }
                }
                else
                    onError.postValue(it.exceptionOrNull())
            }
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