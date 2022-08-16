package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class DefaultResponse {

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("code_error")
    var code: Int? = null

    @SerializedName("message")
    var message: String? = null
}