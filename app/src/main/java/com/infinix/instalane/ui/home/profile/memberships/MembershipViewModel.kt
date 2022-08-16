package com.infinix.instalane.ui.home.profile.memberships

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.request.AddMemberRequest
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MembershipViewModel(application: Application) : BaseViewModel(application) {

    val membershipsLiveData = MutableLiveData<List<Memberships>>()
    val addMembershipsLiveData = MutableLiveData<Memberships>()
    val companyLiveData = MutableLiveData<List<Memberships.Company>>()

    fun getMemberships() =
        viewModelScope.launch {

            ApiClient.service::getMemberships.callApi(AppPreferences.getUser()!!.accessToken!!).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let {
                        membershipsLiveData.postValue(it)
                    }
                }
                else
                    onError.postValue(it.exceptionOrNull())
            }
            /*
            val list = ArrayList<Memberships>()
            for (i in 0.. 3){ list.add(Memberships()) }
            membershipsLiveData.postValue(list)
             */

        }


    fun addMembership(number:String, firstName:String, lastName:String, companyId:String){
        viewModelScope.launch {
            val request = AddMemberRequest().apply {
                this.accessToken = AppPreferences.getUser()!!.accessToken!!
                this.membershipNumber = number
                this.firstName = firstName
                this.lastName = lastName
                this.companyId = companyId
            }
            ApiClient.service::addMembership.callApi(request).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let {
                        addMembershipsLiveData.postValue(it)
                    }
                }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

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