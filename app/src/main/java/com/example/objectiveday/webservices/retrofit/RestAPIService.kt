package com.example.objectiveday.webservices.retrofit

import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.apimodels.APIToken
import com.example.objectiveday.webservices.apimodels.APIUser
import com.example.objectiveday.webservices.apimodels.APIUserDeviceModel
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.net.HttpCookie


class RestAPIService {

    var ip : String = ""
    constructor(ip:String){
        this.ip = ip
    }

    @Throws(Exception::class)
    fun getToken(userModel :APIUserDeviceModel) : APIToken? {
        val retrofit = ServiceBuilder(ip).buildService(RestAPI::class.java)
        try {
            return retrofit.getToken(userModel).execute().body()
        }catch(e:Exception){
            return null
        }
    }

    fun fullRegister(userModel: APIUserDeviceModel, onResult: (APIUser?) -> Unit){
        val retrofit = ServiceBuilder(ip).buildService(RestAPI::class.java)
        retrofit.fullRegister(userModel).enqueue(
            object : retrofit2.Callback<APIUser> {
                override fun onFailure(call: Call<APIUser>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(
                    call: Call<APIUser>,
                    response: Response<APIUser>
                ) {
                    if(response.code()!= 200){
                        onResult(null)
                    }else {
                        val addedUser = response.body()
                        onResult(addedUser)
                    }
                }
            }
        )
    }

    fun getObjectives(apiToken: APIToken?, todo : Boolean?) : List<APIObjectives>{
        if(apiToken == null) return mutableListOf<APIObjectives>()
        System.err.println("GET OBJECTIVE CALL")
        val retrofit = ServiceBuilder(ip).buildService(RestAPI::class.java)
        var deviceName : String = if(apiToken.device == null) "" else apiToken.device
        var token : String = if(apiToken.token == null) "" else apiToken.token

        return try {
            retrofit.getObjectives(deviceName, token, todo).execute().body()
        }catch(e:Exception){
            mutableListOf<APIObjectives>()
        }
    }

    fun saveObjective(apiToken : APIToken, apiObjectives: APIObjectives) : APIObjectives?{
        val retrofit = ServiceBuilder(ip).buildService(RestAPI::class.java)
        var deviceName : String = if(apiToken.device == null) "" else apiToken.device
        var token : String = if(apiToken.token == null) "" else apiToken.token

        return retrofit.saveObjective(apiObjectives, deviceName, token).execute().body()
    }

    fun getObjectiveById(apiToken: APIToken, id : Long) : APIObjectives?{
        val retrofit = ServiceBuilder(ip).buildService(RestAPI::class.java)
        var deviceName : String = if(apiToken.device == null) "" else apiToken.device
        var token : String = if(apiToken.token == null) "" else apiToken.token


        return retrofit.getObjectiveById(deviceName, token, id).execute().body()
    }

    fun markObjectiveAsDone(apiToken: APIToken, id : Long?) : Boolean{
        if(id == null) return false
        val retrofit = ServiceBuilder(ip).buildService(RestAPI::class.java)
        var deviceName : String = if(apiToken.device == null) "" else apiToken.device
        var token : String = if(apiToken.token == null) "" else apiToken.token

        try {
            val code: Int = retrofit.markObjectiveAsDone(deviceName, token, id).execute().code()
            if (code != 200) return false;
            return true;
        }catch(e:Exception){
            System.out.println(e.message)
            return false
        }
    }


}