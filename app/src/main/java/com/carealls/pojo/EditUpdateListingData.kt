package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class EditUpdateListingData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("business_name")
        val businessName: String? = null

        @field:SerializedName("image")
        val image: String? = null

        @field:SerializedName("discription")
        val discription: String? = null

        @field:SerializedName("cat_id")
        val catId: String? = null

        @field:SerializedName("userId")
        val userId: String? = null

    }

}