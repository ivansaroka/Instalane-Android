package com.infinix.instalane.ui.home.checkout

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.SingletonProduct
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.CreateOrderRequest
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : BaseViewModel(application) {

    val productsLiveData = MutableLiveData<List<Product>>()
    val orderLiveData = MutableLiveData<Order>()

    fun getProducts() =
        viewModelScope.launch {
            productsLiveData.postValue(SingletonProduct.instance.productList)
        }

    fun createOrder(subtotal: Float, discount: Float, fee: Float, taxes: Float, total: Float, storeId:String){
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
            }

            ApiClient.service::createOrder.callApi(bodyRequest).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { order ->
                        orderLiveData.postValue(order)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }

        }
    }
}