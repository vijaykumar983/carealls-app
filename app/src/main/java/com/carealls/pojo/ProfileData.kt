package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class ProfileData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: List<ResponseItem?>? = null

    @field:SerializedName("status")
    val status: String? = null

    class ResponseItem {
        @field:SerializedName("address")
        val address: String? = null

        @field:SerializedName("phone")
        val phone: String? = null

        @field:SerializedName("profile")
        val profile: String? = null

        @field:SerializedName("name")
        val name: String? = null

        @field:SerializedName("userId")
        val userId: String? = null

        @field:SerializedName("email")
        val email: String? = null
    }
}
