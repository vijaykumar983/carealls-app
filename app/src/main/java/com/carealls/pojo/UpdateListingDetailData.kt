package com.carealls.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UpdateListingDetailData{
	 @field:SerializedName("msg")
	 val msg: String? = null

	 @field:SerializedName("response")
	 val response: Response? = null

	 @field:SerializedName("status")
	 val status: String? = null


	 class Response{
		 @field:SerializedName("updatelistgalleryimag")
		 val updatelistgalleryimag: List<UpdatelistgalleryimagItem?>? = null

		 @field:SerializedName("productforlistupdate")
		 val productforlistupdate: List<ProductforlistupdateItem?>? = null

		 @field:SerializedName("updatelistdetails")
		 val updatelistdetails: List<UpdatelistdetailsItem?>? = null

		 class UpdatelistgalleryimagItem:Serializable{
			 @field:SerializedName("image")
			 val image: String? = null

			 @field:SerializedName("list_id")
			 val listId: String? = null

			 @field:SerializedName("id")
			 val id: String? = null
		 }

		 class ProductforlistupdateItem:Serializable{
			 @field:SerializedName("listid")
			 val listid: String? = null

			 @field:SerializedName("Id")
			 val id: String? = null

			 @field:SerializedName("product_price")
			 val productPrice: String? = null

			 @field:SerializedName("product_description")
			 val productDescription: String? = null

			 @field:SerializedName("Image")
			 val image: String? = null

			 @field:SerializedName("Name")
			 val name: String? = null
		 }

		 class UpdatelistdetailsItem :Serializable{
			 @field:SerializedName("address")
			 val address: String? = null

			 @field:SerializedName("review")
			 val review: String? = null

			 @field:SerializedName("cat_id")
			 val catId: String? = null

			 @field:SerializedName("category_name")
			 val catName: String? = null

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

	 }

 }
