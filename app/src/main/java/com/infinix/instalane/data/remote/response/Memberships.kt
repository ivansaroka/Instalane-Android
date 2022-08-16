package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Memberships {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("membership_number")
    var membershipNumber: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("company_id")
    var companyId: String? = null

    @SerializedName("client_id")
    var clientId: String? = null

    @SerializedName("company")
    var company: Company? = null

    @SerializedName("verified")
    var verified: Boolean? = null

    class Company{
        @SerializedName("id")
        var id: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("picture")
        var picture: String? = null

        @SerializedName("stores")
        var stores: List<String>? = null

        @SerializedName("memberships")
        var memberships: List<String>? = null

        @SerializedName("membership_required")
        var membershipRequired: Boolean? = null

    }

}