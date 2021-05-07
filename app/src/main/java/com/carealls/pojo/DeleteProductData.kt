package com.carealls.pojo

import com.google.gson.annotations.SerializedName

 class DeleteProductData(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
