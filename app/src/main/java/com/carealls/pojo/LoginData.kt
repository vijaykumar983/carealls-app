package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class LoginData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("profile")
        val profile: Any? = null

        @field:SerializedName("userId")
        val userId: String? = null

        @field:SerializedName("list_id")
        val listId: String? = null

        @field:SerializedName("email")
        val email: String? = null

        @field:SerializedName("username")
        val username: String? = null

        @field:SerializedName("phone")
        val phone: String? = null

        @field:SerializedName("address")
        val address: String? = null

    }
}



