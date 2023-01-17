package com.infinix.instalane.ui.homeGuard.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.User
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch

class GuardProfileViewModel(application: Application) : BaseViewModel(application) {

    val profileLiveData = MutableLiveData<User>()

    fun getProfile() =
        viewModelScope.launch {
            ApiClient.service::getProfile.callApi(AppPreferences.getUser()?.accessToken!!).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { user ->
                        AppPreferences.setUser(user)
                        profileLiveData.postValue(user)
                    }
            }
        }
}