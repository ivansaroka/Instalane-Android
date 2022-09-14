package com.infinix.instalane.data.remote.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("fullname") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("profile_picture") val profilePicture: String? = null,
    @SerializedName("mobile_number") var mobileNumber: String? = null,
    @SerializedName("user_role") var userRole: String? = null,
    @SerializedName("guard_code") var guardCode: String? = null

)

