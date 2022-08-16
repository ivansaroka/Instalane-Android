package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("access_token")
    var accessToken: String? = null,
    @SerializedName("device_token")
    var deviceToken: String? = null
)