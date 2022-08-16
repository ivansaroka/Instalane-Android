package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Coupon {

    @SerializedName("id")
    val id: String? = null

    @SerializedName("code")
    val code: String? = null

    @SerializedName("icon")
    val icon: String? = null

    @SerializedName("store_id")
    val storeId: String? = null

    @SerializedName("product_id")
    val productId: String? = null

    @SerializedName("discount")
    val discount: Float? = null

    @SerializedName("first_purchase")
    val firstPurchase: Boolean? = null

    @SerializedName("init_time")
    val initTime: String? = null

    @SerializedName("finish_time")
    val finishTime: String? = null

    @SerializedName("category")
    val category: Category? = null

}