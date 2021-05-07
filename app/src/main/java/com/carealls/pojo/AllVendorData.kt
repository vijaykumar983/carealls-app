package com.securityadvisoriesalert.pojo

import com.google.gson.annotations.SerializedName

data class AllVendorData(

	@field:SerializedName("ADV3")
	val aDV3: List<ADV3Item>? = null
)

data class ADV3Item(

	@field:SerializedName("Vendor_Name")
	val vendorName: String? = null,

	@field:SerializedName("Vendor_ID")
	val vendorID: String? = null
)
