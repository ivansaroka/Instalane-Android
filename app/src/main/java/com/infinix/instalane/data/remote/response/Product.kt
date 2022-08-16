package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName

class Product {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("stock")
    var stock: Int? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("picture")
    var picture: String? = null

    @SerializedName("price")
    var price: Float? = null

    @SerializedName("prices")
    var priceRegion: List<PriceRegion>? = null

    @SerializedName("sku")
    var sku: String? = null

    @SerializedName("bar_code")
    var barCode: String? = null

    @SerializedName("coupon")
    var coupon: Coupon? = null

    @SerializedName("quantity")
    var quantity: Int? = 1

    var couponApplied :Coupon?=null

    fun hasPriceForThisRegion(region: Store.Region?) : Boolean{
        if (region==null) return false
        priceRegion?.forEach {
            if (it.regionId == region.id && it.price!=null)
                return true
        }
        return false
    }

    fun getPriceByRegion(region: Store.Region?) : Float{
        if (region==null) return 0f
        priceRegion?.forEach {
            if (it.regionId == region.id && it.price!=null)
                return it.price!!
        }
        return 0f
    }

    class PriceRegion{
        @SerializedName("price")
        var price: Float? = null
        @SerializedName("region_id")
        var regionId: String? = null
        @SerializedName("product_id")
        var productId: String? = null
        @SerializedName("createdAt")
        var createdAt: String? = null
        @SerializedName("updatedAt")
        var updatedAt: String? = null
        @SerializedName("id")
        var id: String? = null
    }
}