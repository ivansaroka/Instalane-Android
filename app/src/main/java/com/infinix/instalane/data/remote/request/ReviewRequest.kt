package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class ReviewRequest(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("store_id") val storeId: String? = null,
    @SerializedName("rating") val rating: Int? = null,
    @SerializedName("comment") val comment: String? = null
)

