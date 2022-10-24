package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class User {

    companion object{
        const val ROLE_USER = "user"
        const val ROLE_GUARD = "guard"
    }

    @SerializedName("id")
    var id: String? = null

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("fullname")
    var fullname: String? = null

    @SerializedName("profile_picture")
    var profilePicture: String? = null

    @SerializedName("mobile_number")
    var mobileNumber: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("user_role")
    var userRole: String? = null

    @SerializedName("fb_id")
    var fbId: String? = null
    @SerializedName("fb_token")
    var fbToken: String? = null

    @SerializedName("google_id")
    var googleId: String? = null
    @SerializedName("google_token")
    var googleToken: String? = null

    @SerializedName("device_token")
    var deviceToken: String? = null

    @SerializedName("region_id")
    var region: Region? = null

    fun isUser():Boolean { return userRole == ROLE_USER }

    class Region {
        @SerializedName("id")
        var id: String? = null

        @SerializedName("company_id")
        var company: Company? = null
    }

    class Company{
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("picture")
        var picture: String? = null

    }

}