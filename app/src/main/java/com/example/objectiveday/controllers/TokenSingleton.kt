package com.example.objectiveday.controllers


import com.example.objectiveday.webservices.apimodels.APIToken
import com.example.objectiveday.webservices.apimodels.APIUserDeviceModel
import com.example.objectiveday.webservices.retrofit.RestAPIService

open class TokenSingleton {

    var url : String = ""
    var userDeviceModel: APIUserDeviceModel? = null

    var apiToken : APIToken? = null
    private object HOLDER {
        val INSTANCE = TokenSingleton()
    }

    companion object {
        val instance: TokenSingleton by lazy { HOLDER.INSTANCE }
    }

    fun getToken() : APIToken?{
        if(userDeviceModel == null) return null
        if(isTokenValid()) return this.apiToken
        else{
            val apiService = RestAPIService(url)
            this.apiToken = apiService.getToken(userDeviceModel!!)
            return this.apiToken
        }
    }

    fun isTokenValid() : Boolean{
        return (this.apiToken != null && this.apiToken!!.isValidDateTime())
    }
}