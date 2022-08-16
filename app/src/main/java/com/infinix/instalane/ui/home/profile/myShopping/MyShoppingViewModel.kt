package com.infinix.instalane.ui.home.profile.myShopping

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MyShoppingViewModel(application: Application) : BaseViewModel(application) {

    val myPurchaseHistoryLiveData = MutableLiveData<List<PurchaseHistory>>()
    val orderLiveData = MutableLiveData<Order>()
    private var PAGE = 1

    fun getMyShopping() =
        viewModelScope.launch {
            ApiClient.service::getPurchaseHistory.callApi(AppPreferences.getUser()!!.accessToken!!, PAGE).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { purchase -> myPurchaseHistoryLiveData.postValue(purchase) }
                else
                    onError.postValue(it.exceptionOrNull())
            }
            /*
            val list = ArrayList<PurchaseHistory>()
            for (i in 0.. 3){ list.add(PurchaseHistory()) }
            myPurchaseHistoryLiveData.postValue(list)
             */
        }

    fun getOrder(purchaseHistory: PurchaseHistory) {
        viewModelScope.launch {
            ApiClient.service::getOrder.callApi(AppPreferences.getUser()!!.accessToken!!, purchaseHistory.orderId!!).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { purchase -> orderLiveData.postValue(purchase) }
                else
                    onError.postValue(it.exceptionOrNull())
            }

            /*
            val list = ArrayList<Product>()
            for (i in 0.. 5){ list.add(Product()) }
             */
        }
    }
}