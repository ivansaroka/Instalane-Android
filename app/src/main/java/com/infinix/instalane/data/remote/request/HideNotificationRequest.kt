package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class HideNotificationRequest(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("notification_id") val orderId: String? = null
)

