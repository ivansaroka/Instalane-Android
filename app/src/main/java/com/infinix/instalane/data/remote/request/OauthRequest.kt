package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName
import com.infinix.instalane.data.local.AppPreferences

class OauthRequest {

    @SerializedName("grant_type")
    var grantType: String = "authorization_code"

    @SerializedName("client_id")
    var clientId: String? = AppPreferences.getServerClientId()

    @SerializedName("client_secret")
    var clientSecret: String? = AppPreferences.getClientSecret()

    @SerializedName("redirect_uri")
    var redirectUri: String = ""

    @SerializedName("code")
    var code: String = ""

    @SerializedName("id_token")
    var idToken: String = ""
}