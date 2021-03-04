package com.example.objectiveday.webservices.apimodels

import com.example.objectiveday.Utils
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.time.LocalDateTime

data class APIToken  (
    @SerializedName("device") val device : String? = null,
    @SerializedName("token") val token : String? = null,
    @SerializedName("expire_at") val expireAt : String? = null


) : Serializable{

    fun isValidDateTime() : Boolean{
        if(this.expireAt == null) return false
        var tokenExpirationDT : LocalDateTime? = Utils.stringToDateTime(this.expireAt.toString())
        if(tokenExpirationDT == null) return false

        var now = LocalDateTime.now()
        System.out.println("Token time valid "+now.isBefore(tokenExpirationDT)+ "  - "+now+"  -  "+tokenExpirationDT)
        return LocalDateTime.now().isBefore(tokenExpirationDT)
    }
}