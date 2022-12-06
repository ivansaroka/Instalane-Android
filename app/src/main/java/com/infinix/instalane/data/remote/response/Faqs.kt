package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Faqs {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("question")
    var question: String? = null

    @SerializedName("answer")
    var answer: String? = null

    @SerializedName("type")
    var type: List<String>? = null

}