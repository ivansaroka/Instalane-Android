package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.SendCodeRequest
import com.infinix.instalane.data.remote.request.TwoFactorRequest
import com.infinix.instalane.data.remote.request.ValidateCodeRequest
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.ui.home.profile.myShopping.MyShoppingViewModel
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TwoFactorAuthViewModel(application: Application) : BaseViewModel(application) {

    val sendCodeLiveData = MutableLiveData<Any>()
    val validateCodeLiveData = MutableLiveData<Any>()

    fun sendCode(phone:String, accessToken:String) =
        viewModelScope.launch {
            ApiClient.service::sendCode.callApi(SendCodeRequest(accessToken, phone)).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { purchase -> sendCodeLiveData.postValue(purchase) }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

    fun validateCode(code:String, accessToken:String) {
        viewModelScope.launch {
            ApiClient.service::validateCode.callApi(ValidateCodeRequest(accessToken, code)).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let { sucess ->
                        setTwoFactor(accessToken)
                        //validateCodeLiveData.postValue(sucess)
                    }
                }

                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    private fun setTwoFactor(accessToke:String){
        viewModelScope.launch {
            val request = TwoFactorRequest(
                accessToken = accessToke,
                twofactor = true,
            )

            ApiClient.service::setTwoFactor.callApi(request).collect {
                if (it.isSuccess) {
                    it.getOrNull()?.let { user ->
                        AppPreferences.setUser(user)
                        validateCodeLiveData.postValue("")
                    }
                } else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }
}