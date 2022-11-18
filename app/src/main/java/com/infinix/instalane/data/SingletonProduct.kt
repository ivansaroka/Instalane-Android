package com.infinix.instalane.data

import com.infinix.instalane.data.remote.request.CreateOrderRequest
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store

class SingletonProduct private constructor() {
    var productList = ArrayList<Product>()
    var store : Store?=null

    companion object {
        val instance = SingletonProduct()
    }

    fun getSubtotal():Float{
        var subtotal = 0f
        productList.forEach { prod->
            subtotal+= prod.getPriceByRegion(store?.region)
        }
        return subtotal
    }

    fun getTotalTaxes():Float{
        //price_show_in_the_app - discount * tax / 100
        var taxes = 0f
        productList.forEach { prod->
            if (prod.couponApplied?.discount != null)
                taxes+= (prod.getPriceByRegion(store?.region) - prod.couponApplied!!.discount!!) * prod.getTaxByRegion(store?.region) / 100
            else
                taxes+= (prod.getPriceByRegion(store?.region)) * prod.getTaxByRegion(store?.region) / 100
        }
        return taxes
    }

    fun clearList(){
        productList.clear()
    }

    fun getProductRequest() : ArrayList<CreateOrderRequest.ProductOrderRequest>{

        //check quantity

        productList.forEach { prod->
            val quantityList = productList.filter { it.id == prod.id }
            prod.quantity = quantityList.size
        }

        val listRequest = ArrayList<CreateOrderRequest.ProductOrderRequest>()
        val singleList = productList.distinctBy { it.id }
        singleList.forEach { prod->
            val productRequest = CreateOrderRequest.ProductOrderRequest().apply {
                this.id = prod.id
                this.quantity = prod.quantity
                this.price = prod.getPriceByRegion(store?.region)
            }
            listRequest.add(productRequest)
        }

        return listRequest
    }
}