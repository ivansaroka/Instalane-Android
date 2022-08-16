package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterGoogleRequest(
    @SerializedName("google_id") val googleId: String?,
    @SerializedName("google_token") val googleToken: String? = null
)

