package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterFacebookRequest(
    @SerializedName("fb_id") var fbId: String? = null,
    @SerializedName("fb_token") var fbToken: String? = null
)

