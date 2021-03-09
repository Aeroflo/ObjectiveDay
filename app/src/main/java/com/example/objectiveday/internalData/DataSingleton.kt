package com.example.objectiveday.internalData

import android.content.Context
import com.example.objectiveday.webservices.apimodels.APIObjectives
import java.io.IOException

class DataSingleton {

    private object HOLDER{
        val INSTANCE = DataSingleton()
    }

    companion object {
        val instance : DataSingleton by lazy {HOLDER.INSTANCE}
    }


    val existingObjectives = mutableMapOf<Long, APIObjectives>()
    val newObjectives = mutableMapOf<String, APIObjectives>()

    fun loadObjectiveFromJsonFile(){

    }

    fun getJsonDataFromAsset(context: Context, fileName : String?) : String?{
        if(fileName == null) return null;
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }



    fun generateFileBackUp(){

    }

    fun saveOffline(){

    }

    fun synchToWebservice(){

    }
}