package com.infinix.instalane.ui.home.map

import android.app.Application
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.ApiClient.callApi
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel(application: Application) : BaseViewModel(application) {

    val nearStoreLiveData = MutableLiveData<List<Store>>()

    fun getStores(location: LatLng) =
        viewModelScope.launch {

            val accessToken = AppPreferences.getUser()!!.accessToken!!
            val lat = null//location.latitude
            val long = null//location.longitude
            val radius = null//ConstantValue.RADIUS

            ApiClient.service::getStores.callApi(accessToken,lat, long,radius, null).collect {
                if (it.isSuccess)
                    it.getOrNull()?.let { stores ->
                        stores.forEach { it.calculateDistance(SingletonLocation.instance.myLocation!!) }
                        nearStoreLiveData.postValue(stores)
                    }
                else
                    onError.postValue(it.exceptionOrNull())
            }

            /*
            val list = ArrayList<Store>()
            for (i in 0.. 5){
                val store = Store().apply {
                    id = i.toString()
                    val loc = getRandomLocation(location, 10000)
                    latitude = loc.latitude
                    longitude = loc.longitude
                }
                list.add(store)
            }
            nearStoreLiveData.postValue(list)
             */

        }


    private fun getRandomLocation(point: LatLng, radius: Int): LatLng {
        val randomPoints: MutableList<LatLng> = ArrayList()
        val randomDistances: MutableList<Float> = ArrayList()
        val myLocation = Location("")
        myLocation.latitude = point.latitude
        myLocation.longitude = point.longitude

        //This is to generate 10 random points
        for (i in 0..1) {
            val x0 = point.latitude
            val y0 = point.longitude
            val random = Random()

            // Convert radius from meters to degrees
            val radiusInDegrees = (radius / 111000f).toDouble()
            val u: Double = random.nextDouble()
            val v: Double = random.nextDouble()
            val w = radiusInDegrees * sqrt(u)
            val t = 2 * Math.PI * v
            val x = w * cos(t)
            val y = w * sin(t)

            // Adjust the x-coordinate for the shrinking of the east-west distances
            val new_x = x / cos(y0)
            val foundLatitude = new_x + x0
            val foundLongitude = y + y0
            val randomLatLng = LatLng(foundLatitude, foundLongitude)
            randomPoints.add(randomLatLng)
            val l1 = Location("")
            l1.latitude = randomLatLng.latitude
            l1.longitude = randomLatLng.longitude
            randomDistances.add(l1.distanceTo(myLocation))
        }
        //Get nearest point to the centre
        val indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances))
        return randomPoints[indexOfNearestPointToCentre]
    }
}