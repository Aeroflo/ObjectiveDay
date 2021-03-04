package com.example.objectiveday.webservices.retrofit

import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.example.objectiveday.webservices.apimodels.APIToken
import com.example.objectiveday.webservices.apimodels.APIUser
import com.example.objectiveday.webservices.apimodels.APIUserDeviceModel
import retrofit2.Call
import retrofit2.http.*

interface RestAPI {

    @Headers("Content-Type: application/json")
    @POST("/token")
    fun getToken(@Body userModel: APIUserDeviceModel) : Call<APIToken>

    @Headers("Content-Type: application/json")
    @POST("/fullregister")
    fun fullRegister(@Body userModel: APIUserDeviceModel) : Call<APIUser>


    @GET("/objectives")
    fun getObjectives(@Header("device") device : String, @Header("token") token : String) : Call<List<APIObjectives>>

    @POST("/objectives")
    fun saveObjective(@Body apiObjectives: APIObjectives, @Header("device") device : String, @Header("token")  token: String): Call<APIObjectives>

    @GET("/objective")
    fun getObjectiveById(@Header("device") device : String, @Header("token") token : String, @Query("id") id : Long) : Call<APIObjectives>
}