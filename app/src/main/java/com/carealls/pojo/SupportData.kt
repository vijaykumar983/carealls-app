package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class SupportData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: List<ResponseItem?>? = null

    @field:SerializedName("status")
    val status: String? = null

    class ResponseItem {
        @field:SerializedName("support_contact")
        val supportContact: String? = null

        @field:SerializedName("support_email")
        val supportEmail: String? = null

        @field:SerializedName("support_title")
        val supportTitle: String? = null

        @field:SerializedName("description")
        val description: String? = null
    }

}


