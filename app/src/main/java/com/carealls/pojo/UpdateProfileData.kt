package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class UpdateProfileData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("profile")
        val profile: String? = null

        @field:SerializedName("name")
        val name: String? = null

        @field:SerializedName("userId")
        val userId: String? = null

        @field:SerializedName("email")
        val email: String? = null

        @field:SerializedName("address")
        val address: String? = null
    }
}
