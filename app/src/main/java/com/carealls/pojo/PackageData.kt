package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class PackageData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: List<ResponseItem?>? = null

    @field:SerializedName("status")
    val status: String? = null

    class ResponseItem {
        @field:SerializedName("package_name")
        val packageName: String? = null

        @field:SerializedName("package_id")
        val packageId: String? = null

        @field:SerializedName("package_price")
        val packagePrice: String? = null

        @field:SerializedName("time_period")
        val timePeriod: String? = null
    }

}


