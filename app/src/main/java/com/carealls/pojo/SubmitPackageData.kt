package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class SubmitPackageData {
    @field:SerializedName("data")
    val data: Data? = null

    @field:SerializedName("message")
    val message: String? = null

    @field:SerializedName("status")
    val status: Int? = null

    class Data {
        @field:SerializedName("subscribe_date")
        val subscribeDate: String? = null

        @field:SerializedName("list_id")
        val listId: String? = null

        @field:SerializedName("package_name")
        val packageName: String? = null

        @field:SerializedName("package_id")
        val packageId: String? = null

        @field:SerializedName("userId")
        val userId: String? = null
    }

}
