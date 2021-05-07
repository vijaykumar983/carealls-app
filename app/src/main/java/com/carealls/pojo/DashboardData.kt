package com.carealls.pojo

import com.google.gson.annotations.SerializedName

class DashboardData {
    @field:SerializedName("msg")
    val msg: String? = null

    @field:SerializedName("valid_date")
    val validDate: String? = null

    @field:SerializedName("admin_whatsapp")
    val adminWhatsapp: String? = null

    @field:SerializedName("response")
    val response: Response? = null

    @field:SerializedName("status")
    val status: String? = null

    class Response {
        @field:SerializedName("productforlist")
        val productforlist: List<ProductforlistItem?>? = null

        @field:SerializedName("Banners")
        val banners: List<BannersItem?>? = null

        @field:SerializedName("locationforlisting")
        val locationforlisting: List<LocationforlistingItem?>? = null

        @field:SerializedName("categoryforlisting")
        val categoryforlisting: List<CategoryforlistingItem?>? = null

        @field:SerializedName("listingforhome")
        val listingforhome: List<ListingforhomeItem?>? = null

        class ProductforlistItem {
            @field:SerializedName("listid")
            val listid: String? = null

            @field:SerializedName("Id")
            val id: String? = null

            @field:SerializedName("userId")
            val userId: String? = null

            @field:SerializedName("product_price")
            val productPrice: String? = null

            @field:SerializedName("Image")
            val image: String? = null

            @field:SerializedName("Name")
            val name: String? = null

            @field:SerializedName("whatsapp_number")
            val whatsappNo: String? = null
        }

        class ListingforhomeItem {
            @field:SerializedName("address")
            val address: String? = null

            @field:SerializedName("cat_id")
            val catId: String? = null

            @field:SerializedName("name")
            val name: String? = null

            @field:SerializedName("phone_number")
            val phoneNumber: String? = null

            @field:SerializedName("id")
            val id: String? = null

            @field:SerializedName("listImage")
            val listImage: String? = null

            @field:SerializedName("whatsapp_number")
            val whatsappNumber: String? = null

            @field:SerializedName("review")
            val review : String? = null

            @field:SerializedName("whatappshare")
            val whatsappShare : String? = null
        }

        class BannersItem {
            @field:SerializedName("image")
            val image: String? = null

            @field:SerializedName("link")
            val link: String? = null

            @field:SerializedName("id")
            val id: String? = null

        }

        class CategoryforlistingItem {
            @field:SerializedName("catId")
            val catId: String? = null

            @field:SerializedName("catName")
            val catName: String? = null

            @field:SerializedName("catImage")
            val catImage: String? = null
        }

        class LocationforlistingItem {
            @field:SerializedName("locationId")
            val locationId: String? = null

            @field:SerializedName("Image")
            val image: String? = null

            @field:SerializedName("Name")
            val name: String? = null
        }

    }

}