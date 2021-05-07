package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class SignupData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data(

        @field:SerializedName("userId")
        val userId: Any? = null,

        @field:SerializedName("username")
        val username: String? = null
    )
}



