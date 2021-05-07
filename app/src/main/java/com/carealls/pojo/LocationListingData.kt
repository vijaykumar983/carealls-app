package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class LocationListingData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("locationfilterlist")
        val locationfilterlist: List<LocationfilterlistItem?>? = null

        class LocationfilterlistItem {
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