package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Card {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("photo")
    var photo: String? = null

    @SerializedName("number")
    var number: String? = null

}