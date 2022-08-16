package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class NoteRequest(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("status") val status: Int? = null
)

