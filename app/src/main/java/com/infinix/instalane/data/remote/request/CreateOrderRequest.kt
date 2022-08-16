package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

class CreateOrderRequest {

    @SerializedName("body") var body: Body? = null

    class Body {
        @SerializedName("access_token") var accessToken: String? = null
        @SerializedName("store_id") var storeId: String? = null
        @SerializedName("total") var total: Float? = null
        @SerializedName("sub_total") var subtotal: Float? = null
        @SerializedName("discount") var discount: Float? = null
        @SerializedName("fee") var fee: Float? = null
        @SerializedName("taxes") var taxes: Float? = null
        @SerializedName("products") var products: List<ProductOrderRequest>? = null
    }

    class ProductOrderRequest {
        @SerializedName("id") var id: String? = null
        @SerializedName("quantity") var quantity: Int? = null
        @SerializedName("price") var price: Float? = null
    }
}

