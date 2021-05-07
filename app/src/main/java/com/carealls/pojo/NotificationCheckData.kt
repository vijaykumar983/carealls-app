package com.securityadvisoriesalert.pojo

import com.google.gson.annotations.SerializedName

data class NotificationCheckData(

	@field:SerializedName("ADV4")
	val aDV4: ADV4? = null
)

data class ADV4(

	@field:SerializedName("AdvStatus")
	val advStatus: String? = null
)
