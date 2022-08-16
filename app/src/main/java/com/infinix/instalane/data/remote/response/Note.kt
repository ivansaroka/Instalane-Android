package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Note {

    companion object{
        const val ALL_SCANNED_PAID = 1
        const val ONE_ITEM_NOT_SCANNED_PAID = 2
        const val MORE_ITEM_NOT_SCANNED_PAID = 3
    }

    @SerializedName("id")
    val id: String? = null

    @SerializedName("order_id")
    val orderId: String? = null

    @SerializedName("note")
    val note: String? = null

    @SerializedName("status")
    val status: Int? = null

}