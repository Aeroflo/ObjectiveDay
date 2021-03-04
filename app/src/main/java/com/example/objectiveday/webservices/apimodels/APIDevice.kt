package com.example.objectiveday.webservices.apimodels

import com.google.gson.annotations.SerializedName

data class APIDevice (
    @SerializedName("Name") val name : String? = null
)
