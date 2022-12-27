package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Notification {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("coupon")
    var coupon: Coupon? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("created_datetime")
    var date: String? = null

}