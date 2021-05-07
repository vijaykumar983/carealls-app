package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class SearchData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("searchlisting")
        val searchlisting: List<SearchlistingItem?>? = null

        @field:SerializedName("productsearch")
        val productsearch: List<ProductsearchItem?>? = null

        @field:SerializedName("searchresultsforcategory")
        val searchresultsforcategory: List<SearchresultsforcategoryItem?>? = null


        class SearchlistingItem {
            @field:SerializedName("address")
            val address: String? = null

            @field:SerializedName("review")
            val review: String? = null

            @field:SerializedName("cat_id")
            val catId: String? = null

            @field:SerializedName("name")
            val name: String? = null

            @field:SerializedName("phone_number")
            val phoneNumber: String? = null

            @field:SerializedName("id")
            val id: String? = null

            @field:SerializedName("listImage")
            val listImage: String? = null

            @field:SerializedName("whatsapp_number")
            val whatsappNumber: String? = null
        }

        class SearchresultsforcategoryItem {
            @field:SerializedName("catId")
            val catId: String? = null

            @field:SerializedName("catName")
            val catName: String? = null

            @field:SerializedName("catImage")
            val catImage: String? = null
        }

        class ProductsearchItem {
            @field:SerializedName("listid")
            val listid: String? = null

            @field:SerializedName("Id")
            val id: String? = null

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
