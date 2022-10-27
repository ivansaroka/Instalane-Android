package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class ValidateCodeRequest(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("code") val code: String? = null
)

