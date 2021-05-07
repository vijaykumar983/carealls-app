package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class LocationData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("locationforlisting")
        val locationforlisting: List<LocationforlistingItem?>? = null

        class LocationforlistingItem {
            @field:SerializedName("locationId")
            val locationId: String? = null

            @field:SerializedName("Image")
            val image: String? = null

            @field:SerializedName("Name")
            val name: String? = null

        }
    }
}