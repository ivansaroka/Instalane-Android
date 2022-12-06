package com.infinix.instalane.ui.home.profile.faqs

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Faqs
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch

class FaqsViewModel(application: Application) : BaseViewModel(application) {

    val faqsLiveData = MutableLiveData<List<Faqs>>()

    fun getFaqs() =
        viewModelScope.launch {

            ApiClient.service::getFaqs.callApi().collect {
                if (it.isSuccess){
                    it.getOrNull()?.let {
                        it.filter { !it.type.isNullOrEmpty()  && it.type!!.contains("mobile")}
                        faqsLiveData.postValue(it)
                    }
                }
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
}