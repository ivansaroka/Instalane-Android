package com.infinix.instalane.ui.home.profile.memberships.addMembership

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CompanyViewModel(application: Application) : BaseViewModel(application) {

    val companyLiveData = MutableLiveData<List<Memberships.Company>>()

    fun getCompanies() =
        viewModelScope.launch {
            val accessToken = AppPreferences.getUser()!!.accessToken!!

            ApiClient.service::getCompanies.callApi(accessToken).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { companies ->
                        companyLiveData.postValue(companies)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }

}