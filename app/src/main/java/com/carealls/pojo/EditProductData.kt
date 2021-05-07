package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class EditProductData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("image")
        val image: String? = null

        @field:SerializedName("discription")
        val discription: String? = null

        @field:SerializedName("product_id")
        val productId: String? = null

        @field:SerializedName("product_name")
        val productName: String? = null
    }


}
