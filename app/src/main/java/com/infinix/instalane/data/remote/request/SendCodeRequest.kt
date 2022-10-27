package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class SendCodeRequest(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("mobile_number") val mobileNumber: String? = null
)

