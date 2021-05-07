package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class CategoryProductData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("productforcategory")
        val productforcategory: List<ProductforcategoryItem?>? = null

        class ProductforcategoryItem {
            @field:SerializedName("listid")
            val listid: String? = null

            @field:SerializedName("Id")
            val id: String? = null

            @field:SerializedName("userId")
            val userId: String? = null

            @field:SerializedName("product_price")
            val productPrice: String? = null

            @field:SerializedName("Image")
            val image: String? = null

            @field:SerializedName("Name")
            val name: String? = null

            @field:SerializedName("whatsapp_number")
            val whatsappNo: String? = null
        }

    }

}