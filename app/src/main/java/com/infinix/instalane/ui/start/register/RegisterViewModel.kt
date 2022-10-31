package com.infinix.instalane.ui.start.register

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.RegisterRequest
import com.infinix.instalane.data.remote.response.Legal
import com.infinix.instalane.data.remote.response.User
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : BaseViewModel(application) {

    val registerLiveData = MutableLiveData<Result<User>>()
    val privacyLiveData = MutableLiveData<Legal>()
    val termsLiveData = MutableLiveData<Legal>()

    fun register(fullName: String, email: String, password: String, phoneNumber: String, role: String, guardCode:String) =
        viewModelScope.launch {
            val request = RegisterRequest(
                fullName = fullName,
                email = email,
                password = password,
                mobileNumber = phoneNumber,
                userRole = role,
                guardCode = guardCode
            )

            ApiClient.service::register.callApi(request).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let { user -> AppPreferences.setUser(user) }
                    registerLiveData.postValue(it)
                }else{
                    onError.postValue(it.exceptionOrNull())
                }
            }
        }

    fun getPrivacy(){
        viewModelScope.launch {
            ApiClient.service::getPrivacy.callApi().collect {
                if (it.isSuccess){
                    it.getOrNull()?.let { privacyLiveData.postValue(it) }
                } else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    fun getTerms(){
        viewModelScope.launch {
            ApiClient.service::getTerms.callApi().collect {
                if (it.isSuccess){
                    it.getOrNull()?.let { termsLiveData.postValue(it) }
                } else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }
}