package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class ForgotPassData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("otp")
        val otp: Int? = null

        @field:SerializedName("userId")
        val userId: String? = null
    }

}
