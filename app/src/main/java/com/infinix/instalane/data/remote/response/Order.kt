package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName


class Order {

    companion object {
        const val STATE_DELIVERED = "delivered"
        const val STATE_PAID = "paid"
        const val STATE_FAILED = "failed"
    }

    @SerializedName("id")
    var id: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("amount")
    var amount: Float? = 0f
    @SerializedName("status")
    var status: String? = null
    @SerializedName("sub_total")
    var subTotal: Float? = 0f
    @SerializedName("fee")
    var fee: Float? = 0f
    @SerializedName("discount")
    var discount: Float? = 0f
    @SerializedName("taxes")
    var taxes: Float? = 0f
    @SerializedName("store")
    var store: Store? = null
    @SerializedName("createdAt")
    var createdAt: String? = null
    @SerializedName("updatedAt")
    var updatedAt: String? = null
    @SerializedName("items")
    var items: List<ItemOrder>? = null
    @SerializedName("notes")
    var notes: List<Note>? = null
    @SerializedName("client")
    var client: User? = null
    @SerializedName("guard")
    var guard: User? = null

    class ItemOrder {
        @SerializedName("id")
        var id: String? = null
        @SerializedName("qty")
        var qty: Int? = 0
        @SerializedName("confirmed")
        var confirmed: Boolean? = false
        @SerializedName("price")
        var price: Float? = null
        @SerializedName("product")
        var product: Product? = null
    }
}