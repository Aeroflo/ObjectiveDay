package com.example.objectiveday.webservices.apimodels

import com.google.gson.annotations.SerializedName

data class APIUser(
    @SerializedName("id") val username : String? = null,
    @SerializedName("username") val deviceName : String? = null,
    @SerializedName("Devices") val devices : List<APIDevice>? = null
)
