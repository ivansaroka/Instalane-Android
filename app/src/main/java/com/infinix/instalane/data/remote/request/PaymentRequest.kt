package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

class PaymentRequest {
    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("order_id")
    var orderId: String? = null

    @SerializedName("amount")
    var amount: Int? = null
}