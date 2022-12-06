package com.infinix.instalane.ui.home.profile.myShopping

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.ReviewRequest
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch

class MyShoppingViewModel(application: Application) : BaseViewModel(application) {

    val myPurchaseHistoryLiveData = MutableLiveData<List<PurchaseHistory>>()
    val orderLiveData = MutableLiveData<Order>()
    val addReviewLiveData = MutableLiveData<Any>()
    private var PAGE = 1

    fun getMyShopping() =
        viewModelScope.launch {
            ApiClient.service::getPurchaseHistory.callApi(AppPreferences.getUser()!!.accessToken!!, PAGE).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { purchase -> myPurchaseHistoryLiveData.postValue(purchase.sortedByDescending { it.date }) }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun getOrder(purchaseHistory: PurchaseHistory) {
        viewModelScope.launch {
            ApiClient.service::getOrder.callApi(AppPreferences.getUser()!!.accessToken!!, purchaseHistory.orderId!!).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { purchase -> orderLiveData.postValue(purchase) }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    fun addReview(store: Store, stars:Int, comment:String) {
        viewModelScope.launch {
            val accessToken=AppPreferences.getUser()!!.accessToken
            val request =  ReviewRequest(accessToken, store.id, stars, comment)
            ApiClient.service::addReview.callApi(request).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { reviews -> addReviewLiveData.postValue("") }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }
}