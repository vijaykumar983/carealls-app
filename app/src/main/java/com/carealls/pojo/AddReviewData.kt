package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class AddReviewData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("discription")
        val discription: String? = null

        @field:SerializedName("list_id")
        val listId: String? = null

        @field:SerializedName("review")
        val review: String? = null

        @field:SerializedName("user")
        val user: String? = null

        @field:SerializedName("userId")
        val userId: String? = null
    }

}