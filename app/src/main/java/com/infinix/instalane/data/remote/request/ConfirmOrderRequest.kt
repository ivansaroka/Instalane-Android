package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class ConfirmOrderRequest(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("item_ids") val list: List<String>? = null
)

