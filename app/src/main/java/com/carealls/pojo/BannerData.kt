package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class BannerData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("Banners")
        val banners: List<BannersItem?>? = null

        class BannersItem {
            @field:SerializedName("image")
            val image: String? = null

            @field:SerializedName("link")
            val link: String? = null

            @field:SerializedName("id")
            val id: String? = null

        }
    }


}