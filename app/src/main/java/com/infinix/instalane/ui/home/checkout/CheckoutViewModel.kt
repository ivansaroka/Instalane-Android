package com.infinix.instalane.ui.home.checkout

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.SingletonProduct
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.CreateOrderRequest
import com.infinix.instalane.data.remote.request.PaymentRequest
import com.infinix.instalane.data.remote.response.*
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : BaseViewModel(application) {

    val productsLiveData = MutableLiveData<List<Product>>()
    val orderLiveData = MutableLiveData<Order>()
    val paymentIntentLiveData = MutableLiveData<PaymentIntentResponse>()
    val couponLiveData = MutableLiveData<List<Coupon>>()
    val updateOrderLiveData = MutableLiveData<Order>()

    fun getProducts() =
        viewModelScope.launch {
            productsLiveData.postValue(SingletonProduct.instance.productList)
        }

    fun getCoupons(store: Store) =
        viewModelScope.launch {
            ApiClient.service::getCoupons.callApi(AppPreferences.getUser()!!.accessToken!!, store.id, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores ->
                        couponLiveData.postValue(stores.filter { it.firstPurchase != true })
                    }
                else
                    couponLiveData.postValue(ArrayList())
            }
        }

    fun createOrder(subtotal: Float, discount: Float, fee: Float, taxes: Float, total: Float, storeId:String, couponIds : ArrayList<String>){
        viewModelScope.launch {
            val bodyRequest = CreateOrderRequest.Body().apply {
                this.accessToken = AppPreferences.getUser()!!.accessToken!!
                this.storeId = storeId
                this.subtotal = subtotal
                this.discount = discount
                this.fee = fee
                this.taxes = taxes
                this.total = total
                this.products = SingletonProduct.instance.getProductRequest()
                this.couponIds = couponIds
            }

            ApiClient.service::createOrder.callApi(bodyRequest).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { order ->
                        orderLiveData.postValue(order)
                        pay(order)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }

        }
    }


    private fun pay(order: Order){
        viewModelScope.launch {
            val request = PaymentRequest().apply {
                this.accessToken = AppPreferences.getUser()!!.accessToken
                this.orderId = order.id
                this.amount = order.amount!!.times(100).toInt()
            }
            ApiClient.service::paymentIntent.callApi(request).collect{
                if(it.isSuccess)
                    it.getOrNull()?.let { paymentIntentLiveData.postValue(it) }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    fun getOrder(orderID:String) {
        viewModelScope.launch {
            ApiClient.service::getOrder.callApi(AppPreferences.getUser()!!.accessToken!!, orderID).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { order ->
                        updateOrderLiveData.postValue(order)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }
}