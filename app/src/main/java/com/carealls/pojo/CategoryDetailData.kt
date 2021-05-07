package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class CategoryDetailData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("categoryforlisting")
        val categoryforlisting: List<CategoryforlistingItem?>? = null

        @field:SerializedName("categoryfordetails")
        val categoryfordetails: List<CategoryfordetailsItem?>? = null

        class CategoryfordetailsItem {
            @field:SerializedName("catId")
            val catId: String? = null

            @field:SerializedName("catName")
            val catName: String? = null

            @field:SerializedName("catImage")
            val catImage: String? = null
        }

        class CategoryforlistingItem {
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

    }

}

