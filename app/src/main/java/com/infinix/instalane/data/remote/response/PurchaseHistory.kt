package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class PurchaseHistory {

    @SerializedName("order_id")
    var orderId: String? = null

    @SerializedName("store")
    var store: Store? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("amount")
    var amount: Float? = null

}