package com.infinix.instalane.ui.start.register

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.RegisterRequest
import com.infinix.instalane.data.remote.response.User
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : BaseViewModel(application) {

    val registerLiveData = MutableLiveData<Result<User>>()


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
                if (it.isSuccess) it.getOrNull()?.let { user -> AppPreferences.setUser(user) }
                registerLiveData.postValue(it)
            }
        }
}