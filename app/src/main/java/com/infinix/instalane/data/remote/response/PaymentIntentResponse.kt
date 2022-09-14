package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class PaymentIntentResponse {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("paymentIntent")
    var paymentIntent: String? = null

    @SerializedName("ephemeralKey")
    var ephemeralKey: String? = null

    @SerializedName("customer")
    var customer: String? = null

    @SerializedName("publishableKey")
    var publishableKey: String? = null
}