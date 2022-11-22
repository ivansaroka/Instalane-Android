package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class TwoFactorRequest(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("twofactor") val twofactor: Boolean? = null,
    @SerializedName("mobile_number") val mobileNumber: String? = null
)

