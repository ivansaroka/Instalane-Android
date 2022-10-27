package com.infinix.instalane.ui.home.barcode

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class ProductViewModel(application: Application) : BaseViewModel(application) {

    val productLiveData = MutableLiveData<Product>()

    fun getProduct(code:String, store: Store) =
        viewModelScope.launch {

            if (store.region?.company?.id.isNullOrEmpty()){
                onError.postValue(Throwable("This store doesn't have a related company"))
                return@launch
            }

            ApiClient.service::getProduct.callApi(AppPreferences.getUser()!!.accessToken!!, store.region?.company?.id!! , null, code).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { product ->
                        if (product.isEmpty())
                            onError.postValue(Throwable("This product is not available for this store in this region"))
                            //onError.postValue(it.exceptionOrNull())
                        else {
                            if (product[0].hasPriceForThisRegion(store.region))
                                productLiveData.postValue(product[0])
                            else
                                onError.postValue(Throwable("This product is not available for this store in this region"))
                        }
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
}