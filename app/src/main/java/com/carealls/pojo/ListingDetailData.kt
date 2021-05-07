package com.carealls.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ListingDetailData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null


    class Response {
        @field:SerializedName("productforcatalog")
        val productforcatalog: List<ProductforcatalogItem?>? = null

        @field:SerializedName("listingfordetails")
        val listingfordetails: List<ListingfordetailsItem?>? = null

        class ListingfordetailsItem {
            @field:SerializedName("user_id")
            val userId: String? = null

            @field:SerializedName("address")
            val address: String? = null

            @field:SerializedName("review")
            val review: String? = null

            @field:SerializedName("cat_id")
            val catId: String? = null

            @field:SerializedName("name")
            val name: String? = null

            @field:SerializedName("description")
            val description: String? = null

            @field:SerializedName("phone_number")
            val phoneNumber: String? = null

            @field:SerializedName("id")
            val id: String? = null

            @field:SerializedName("listImage")
            val listImage: String? = null

            @field:SerializedName("whatsapp_number")
            val whatsappNumber: String? = null
        }

        class ProductforcatalogItem :Serializable{
            @field:SerializedName("listid")
            val listid: String? = null

            @field:SerializedName("Id")
            val id: String? = null

            @field:SerializedName("product_price")
            val productPrice: String? = null

            @field:SerializedName("Image")
            val image: String? = null

            @field:SerializedName("Name")
            val name: String? = null
        }


    }

}

