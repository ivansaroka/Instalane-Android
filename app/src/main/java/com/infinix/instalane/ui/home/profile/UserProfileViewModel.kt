package com.infinix.instalane.ui.home.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.EditRequest
import com.infinix.instalane.data.remote.request.LogoutRequest
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class UserProfileViewModel(application: Application) : BaseViewModel(application) {

    val logoutLiveData = MutableLiveData<Any>()
    val deleteLiveData = MutableLiveData<Any>()
    val editLiveData = MutableLiveData<Any>()

    fun logout() =
        viewModelScope.launch {
            val request = LogoutRequest(
                accessToken = AppPreferences.getUser()?.accessToken,
                deviceToken = AppPreferences.getDeviceToken()
            )
            ApiClient.service::logout.callApi(request).collect { logoutLiveData.postValue("") }
        }

    fun deleteAccount() = viewModelScope.launch {
        val request = LogoutRequest(
            accessToken = AppPreferences.getUser()?.accessToken,
            deviceToken = null
        )
        ApiClient.service::deleteAccount.callApi(request).collect {
            if (it.isSuccess)
                deleteLiveData.postValue("")
            else
                onError.postValue(it.exceptionOrNull())
        }
    }

    fun editUser(fullName: String, email: String, mobileNumber: String, fileSelected: File?) {
        viewModelScope.launch {
            val request = EditRequest(
                accessToken = AppPreferences.getUser()?.accessToken,
                fullName = fullName.ifEmpty { null },
                email = email.ifEmpty { null },
                mobileNumber = mobileNumber.ifEmpty { null },
            )

            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("access_token", AppPreferences.getUser()?.accessToken!!)
                .addFormDataPart("fullname", fullName.ifEmpty { "" })
                .addFormDataPart("email", email.ifEmpty { "" })
                .addFormDataPart("mobile_number", mobileNumber.ifEmpty { "" })
            if (fileSelected!=null){
                multipartBody.addFormDataPart("profile_picture", fileSelected.name, fileSelected.asRequestBody())
            }

            ApiClient.service::editProfile.callApi(multipartBody.build()).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let { user -> AppPreferences.setUser(user) }
                    editLiveData.postValue(it)
                } else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }
}