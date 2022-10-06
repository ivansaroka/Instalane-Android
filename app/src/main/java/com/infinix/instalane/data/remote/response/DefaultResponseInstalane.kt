package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class DefaultResponseInstalane {

    @SerializedName("code")
    var code: String? = null

    @SerializedName("message")
    var message: MessageError? = null

    class MessageError{
        @SerializedName("email")
        var email: List<String>? = null

        @SerializedName("guard_code")
        var guardCode: List<String>? = null
    }
}