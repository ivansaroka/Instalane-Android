package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class OauthResponse {

    @SerializedName("access_token")
    val accessToken: String? = null

    @SerializedName("expires_in")
    var expiresIn: Int? = null

    @SerializedName("refresh_token")
    var refreshToken: String? = null

    @SerializedName("token_type")
    val tokenType: String? = null

    @SerializedName("id_token")
    var idToken: String = ""
}