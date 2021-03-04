package com.example.objectiveday.webservices.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceBuilder {

    private var ip : String = ""
    constructor(ip : String){
        this.ip = ip
    }

    private val client = OkHttpClient.Builder()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.73:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build())
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}