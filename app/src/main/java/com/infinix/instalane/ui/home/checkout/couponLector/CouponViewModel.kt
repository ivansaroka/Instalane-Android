package com.infinix.instalane.ui.home.checkout.couponLector

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CouponViewModel(application: Application) : BaseViewModel(application) {

    val couponLiveData = MutableLiveData<Coupon>()

    fun getCoupon(storeID:String,code:String) =
        viewModelScope.launch {

            ApiClient.service::getCoupons.callApi(AppPreferences.getUser()!!.accessToken!!, storeID, code).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { product ->
                        if (product.isEmpty())
                            onError.postValue(it.exceptionOrNull())
                        else
                            couponLiveData.postValue(product[0])
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

}