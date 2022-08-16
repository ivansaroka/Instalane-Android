package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class DeviceTokenRequest(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("device_token") val deviceToken: String? = null,
    @SerializedName("device_type") val deviceType: String = "android"
)

