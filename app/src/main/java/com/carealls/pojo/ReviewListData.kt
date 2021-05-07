package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class ReviewListData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("reviewuser")
        val reviewuser: List<ReviewuserItem?>? = null

        @field:SerializedName("listingforreview")
        val listingforreview: List<ListingforreviewItem?>? = null

        class ReviewuserItem {
            @field:SerializedName("profile")
            val profile: String? = null

            @field:SerializedName("name")
            val name: String? = null

            @field:SerializedName("id")
            val id: String? = null
        }

        class ListingforreviewItem {
            @field:SerializedName("list_id")
            val listId: String? = null

            @field:SerializedName("user_id")
            val userId: String? = null

            @field:SerializedName("star_rating")
            val starRating: String? = null

            @field:SerializedName("description")
            val description: String? = null

            @field:SerializedName("id")
            val id: String? = null

            @field:SerializedName("user")
            val user: String? = null
        }

    }
}