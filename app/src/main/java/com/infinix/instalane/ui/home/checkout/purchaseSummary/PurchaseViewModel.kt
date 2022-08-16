package com.infinix.instalane.ui.home.checkout.purchaseSummary

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.SingletonProduct
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.ConfirmOrderRequest
import com.infinix.instalane.data.remote.request.NoteRequest
import com.infinix.instalane.data.remote.request.ReviewRequest
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PurchaseViewModel(application: Application) : BaseViewModel(application) {

    val productsLiveData = MutableLiveData<List<Product>>()
    val orderLiveData = MutableLiveData<Order>()
    val addReviewLiveData = MutableLiveData<Any>()
    val addNoteLiveData = MutableLiveData<Any>()
    val confirmOrderLiveData = MutableLiveData<Any>()

    fun getProducts() =
        viewModelScope.launch {
            productsLiveData.postValue(SingletonProduct.instance.productList)
        }

    fun getOrder(orderId:String){
        viewModelScope.launch {
            ApiClient.service::getOrder.callApi(AppPreferences.getUser()!!.accessToken!!, orderId).collect {
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

    fun addReview(store:Store, stars:Int, comment:String) {
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

    fun addNote(orderId:String, note:String, status:Int){
        viewModelScope.launch {
            val accessToken=AppPreferences.getUser()!!.accessToken
            val request =  NoteRequest(accessToken, orderId, note, status)
            ApiClient.service::addNote.callApi(request).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { addNoteLiveData.postValue("") }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    fun confirmOrder(orderId:String, listItemOrder :List<Order.ItemOrder>){
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken
            val list = listItemOrder.map { it.id!! }
            val request =  ConfirmOrderRequest(accessToken, orderId, list)
            ApiClient.service::confirmOrder.callApi(request).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { confirmOrderLiveData.postValue("") }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }
}