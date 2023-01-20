package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class ManyProductsRequest(
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("product_ids") var products: List<String>? = null
)

