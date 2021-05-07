package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class AllCategoryData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("categoryforlisting")
        val categoryforlisting: List<CategoryforlistingItem?>? = null

        @field:SerializedName("bannerimage")
        val bannerimage: String? = null

        class CategoryforlistingItem {
            @field:SerializedName("catId")
            val catId: String? = null

            @field:SerializedName("catName")
            val catName: String? = null

            @field:SerializedName("catImage")
            val catImage: String? = null
        }

    }

}
