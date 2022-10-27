package com.infinix.instalane.data.remote.response

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName


class Store {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("picture")
    var picture: String? = null

    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("best_discount")
    var bestDiscount: Int? = null

    @SerializedName("lat")
    var latitude: Double? = null

    @SerializedName("long")
    var longitude: Double? = null

    @SerializedName("review_count")
    var reviewCount: ReviewCount? = null

    @SerializedName("can_open")
    var canOpen: Boolean? = null

    var distance: Float = 0f

    @SerializedName("region_id")
    var region: Region? = null

    @SerializedName("region_tax")
    var regionTax: String? = null

    class Region {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("guard_code")
        var guardCode: String? = null

        @SerializedName("company_id")
        var company: Memberships.Company? = null
    }

    class ReviewCount {
        @SerializedName("review_count_1")
        val reviewCount1 : Int = 0
        @SerializedName("review_count_2")
        val reviewCount2: Int = 0
        @SerializedName("review_count_3")
        val reviewCount3: Int = 0
        @SerializedName("review_count_4")
        val reviewCount4: Int = 0
        @SerializedName("review_count_5")
        val reviewCount5: Int = 0
    }

    fun calculateRate() : Float {
        if (reviewCount==null) return 0f
        val total = reviewCount!!.reviewCount1 + (reviewCount!!.reviewCount2 * 2) + (reviewCount!!.reviewCount3 * 3)
        + (reviewCount!!.reviewCount4 * 4) + (reviewCount!!.reviewCount5 * 5)

        return total/5f
    }

    fun calculateDistance(latLng: LatLng): Float {
        val loc = getLocation()
        val MILE = 0.621371f
        if (loc != null) {
            val locationA = Location("point A")

            locationA.latitude = loc.latitude
            locationA.longitude = loc.longitude

            val locationB = Location("point B")

            locationB.latitude = latLng.latitude
            locationB.longitude = latLng.longitude

            distance = locationA.distanceTo(locationB) / 1000


        }
        return distance * MILE
    }

    fun hasLocation():Boolean {
        if (latitude == null || longitude == null)
            return false
        return true
    }

    fun getLocation(): LatLng? {
        if (latitude!=null && longitude!=null) {
            return LatLng(latitude!!.toDouble(), longitude!!.toDouble())
        }
        return null
    }
}