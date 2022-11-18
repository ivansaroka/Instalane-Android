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

    @SerializedName("quantity")
    var quantity: Int? = 1

    @SerializedName("related_coupons")
    var relatedCoupons: List<RelatedCoupon>? = null

    var couponApplied :Coupon?=null
    var coupon: Coupon? = null
    var isSponsoredBrand: Boolean = false

    fun hasPriceForThisRegion(region: Store.Region?) : Boolean{
        if (region==null) return false
        priceRegion?.forEach {
            if (it.regionId == region.id && it.priceShowInApp!=null)
                return true
        }
        return false
    }

    fun getPriceByRegion(region: Store.Region?) : Float{
        if (region==null) return 0f
        priceRegion?.forEach {
            if (it.regionId == region.id && it.priceShowInApp!=null)
                return it.priceShowInApp!!
        }
        return 0f
    }

    fun getTaxByRegion(region: Store.Region?) : Float{
        if (region==null) return 0f
        priceRegion?.forEach {
            if (it.regionId == region.id && it.taxPercent!=null)
                return it.taxPercent!!
        }
        return 0f
    }

    fun hasCoupons():Boolean{
        return coupon!=null
    //return !relatedCoupons.isNullOrEmpty()
    }

    class PriceRegion{
        @SerializedName("price")
        var price: Float? = null
        @SerializedName("price_shown_in_the_app")
        var priceShowInApp: Float? = null
        @SerializedName("region_id")
        var regionId: String? = null
        @SerializedName("tax_percent")
        var taxPercent: Float? = null
        @SerializedName("product_id")
        var productId: String? = null
        @SerializedName("createdAt")
        var createdAt: String? = null
        @SerializedName("updatedAt")
        var updatedAt: String? = null
        @SerializedName("id")
        var id: String? = null
    }

    class RelatedCoupon{
        @SerializedName("id")
        var id: String? = null
        @SerializedName("product_id")
        var productId: String? = null
        @SerializedName("coupon_id")
        var couponId: String? = null
    }
}