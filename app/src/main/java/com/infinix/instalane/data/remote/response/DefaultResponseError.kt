package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class DefaultResponseError {

    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: String? = null

}