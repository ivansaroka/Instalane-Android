package com.infinix.instalane.data

import com.google.android.gms.maps.model.LatLng

class SingletonLocation private constructor() {

    var myLocation : LatLng?=null
    var locationSelected : LatLng?=null

    companion object {
        val instance = SingletonLocation()
    }

    fun getLocation():LatLng? {
        if (locationSelected!=null)
            return locationSelected!!
        return myLocation
    }
}