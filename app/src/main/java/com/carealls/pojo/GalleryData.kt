package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class GalleryData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: List<ResponseItem?>? = null

    @field:SerializedName("status")
    val status: String? = null


    class ResponseItem(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("list_id")
        val listId: String? = null,

        @field:SerializedName("id")
        val Id: String? = null
    )

}