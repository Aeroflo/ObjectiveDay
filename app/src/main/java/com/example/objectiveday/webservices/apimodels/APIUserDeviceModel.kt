package com.example.objectiveday.webservices.apimodels

import com.google.gson.annotations.SerializedName


data class APIUserDeviceModel(
       @SerializedName("username") val username : String? = null,
       @SerializedName("deviceName") val deviceName : String? = null,
       @SerializedName("pass") val password : String? = null,
       @SerializedName("email") val email : String? = null
)

