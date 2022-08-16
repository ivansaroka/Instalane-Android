package com.infinix.instalane.ui.home.profile.paymentMethods

import android.app.Application
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.infinix.instalane.data.remote.response.Card
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PaymentViewModel(application: Application) : BaseViewModel(application) {

    val cardLiveData = MutableLiveData<List<Card>>()

    fun getCards() =
        viewModelScope.launch {
            val list = ArrayList<Card>()
            for (i in 0.. 3){ list.add(Card()) }
            cardLiveData.postValue(list)
        }

}