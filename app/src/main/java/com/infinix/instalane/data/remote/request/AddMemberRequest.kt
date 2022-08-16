package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class AddMemberRequest(
    @SerializedName("access_token") var accessToken: String? = null,
    @SerializedName("membership_number") var membershipNumber: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("company_id") var companyId: String? = null
)

