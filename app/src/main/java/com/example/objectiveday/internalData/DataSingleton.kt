package com.example.objectiveday.internalData

import android.content.Context
import com.example.objectiveday.Utils
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.objective_main_object_layout.view.*
import java.io.*
import java.nio.file.Files
import java.time.DayOfWeek
import java.time.LocalDateTime

class DataSingleton {

    private object HOLDER{
        val INSTANCE = DataSingleton()
    }

    companion object {
        val instance : DataSingleton by lazy {HOLDER.INSTANCE}
    }


    val existingObjectives = mutableMapOf<Long, APIObjectives>()
    val newObjectives = mutableMapOf<String, APIObjectives>()
    var offlineMode : Boolean = true


    //FUN
    //Load objectives
    fun loadAPIObjectives(context: Context){
        if(!offlineMode){
            //load from file
            //load from webservice
            //synch
        }else{
            loadAPIObjectivesLocal(context)
        }
    }

    fun getData() :List<APIObjectives>{
        var listToReturn = mutableListOf<APIObjectives>()
        existingObjectives.forEach{k, v ->
            listToReturn.add(v)
        }
        newObjectives.forEach{ k, v ->
            listToReturn.add(v)
        }
        return listToReturn.toList()
    }

    fun getObjectifTodoToday() : List<APIObjectives>{
        var listToReturn = mutableListOf<APIObjectives>()
        var now = LocalDateTime.now()
        var dayOfWeek = now.dayOfWeek
        existingObjectives.values.forEach{o ->

            when (dayOfWeek) {
                DayOfWeek.MONDAY -> if (o.monday != null && o.monday) listToReturn.add(o)
                DayOfWeek.TUESDAY -> if (o.tuesday != null && o.tuesday) listToReturn.add(o)
                DayOfWeek.WEDNESDAY -> if (o.wednesday != null && o.wednesday) listToReturn.add(o)
                DayOfWeek.THURSDAY -> if (o.thurday != null && o.thurday) listToReturn.add(o)
                DayOfWeek.FRIDAY -> if (o.friday != null && o.friday) listToReturn.add(o)
                DayOfWeek.SATURDAY -> if (o.saturday != null && o.saturday) listToReturn.add(o)
                DayOfWeek.SUNDAY -> if (o.sunday != null && o.sunday) listToReturn.add(o)
            }
        }

        newObjectives.values.forEach{o ->

            when (dayOfWeek) {
                DayOfWeek.MONDAY -> if (o.monday != null && o.monday) listToReturn.add(o)
                DayOfWeek.TUESDAY -> if (o.tuesday != null && o.tuesday) listToReturn.add(o)
                DayOfWeek.WEDNESDAY -> if (o.wednesday != null && o.wednesday) listToReturn.add(o)
                DayOfWeek.THURSDAY -> if (o.thurday != null && o.thurday) listToReturn.add(o)
                DayOfWeek.FRIDAY -> if (o.friday != null && o.friday) listToReturn.add(o)
                DayOfWeek.SATURDAY -> if (o.saturday != null && o.saturday) listToReturn.add(o)
                DayOfWeek.SUNDAY -> if (o.sunday != null && o.sunday) listToReturn.add(o)
            }
        }

        return listToReturn
    }

    fun flushData(){
        //flush memory
        //flush file
    }

    fun loadAPIObjectivesLocal(context: Context){
        var localObjective = readAPIObjectiveFromLocalFile(context)
        if(!localObjective.isNullOrEmpty()){
            localObjective.forEach{local ->
                updateListData(local)
            }
        }
    }

    fun saveAPIObjectiveLocal(context: Context, apiObjectives: APIObjectives) : APIObjectives?{
        updateListData(apiObjectives)
        var saved = writeAllInAJsonFile(context)
        if(saved) return apiObjectives
        else return null;
    }

    fun markObjectiveAsDone(context: Context, objectiveModel: ObjectiveModel?) : APIObjectives?{
        var now = LocalDateTime.now()
        if(objectiveModel != null){
            var apiObjectiveSaved : APIObjectives?  = APIObjectives(objectiveModel.id, objectiveModel.parentId, objectiveModel.description,
                objectiveModel.isMonday, objectiveModel.isTuesday, objectiveModel.isWednesday, objectiveModel.isThursday, objectiveModel.isFriday, objectiveModel.isSaturday, objectiveModel.isSunday,
                objectiveModel.time.toString(), objectiveModel.isNotifiable, null, null, null, objectiveModel.isActive, null, now.toString())
            apiObjectiveSaved =  saveAPIObjectiveLocal(context, apiObjectiveSaved!!)
            return apiObjectiveSaved
        }
        return null
    }

    fun updateListData(apiObjectives: APIObjectives){
        if(apiObjectives.id != null) existingObjectives.put(apiObjectives.id, apiObjectives)
        else if(!apiObjectives.description.isNullOrBlank()) newObjectives.put(apiObjectives.description, apiObjectives )
    }

    fun synchToWebservice(context: Context){
        if(!offlineMode){

        }
    }

    //Works
    fun writeAllInAJsonFile(context: Context) : Boolean{



        val list = getData()
        val gson = Gson()

        val json: String = gson.toJson(list)


        try {
            val outStringBuf = StringBuffer()

            val outputStreamWriter = OutputStreamWriter(
                context.openFileOutput(
                    "test.txt",
                    Context.MODE_PRIVATE
                )
            )
            outputStreamWriter.write(json)
            outputStreamWriter.close()
            return true
        } catch (e: IOException) {
            System.out.println(e.message)
            return false
        }


    }

    //Works
    private fun readAPIObjectiveFromLocalFile(context: Context) : List<APIObjectives>{
        var ret = ""
        try {
            val inputStream: InputStream = context.openFileInput("test.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also({ receiveString = it }) != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            //Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            //Log.e("login activity", "Can not read file: $e")
        }
         return apiObjectiveJSONToObject(ret)
    }

    //Works
    private fun apiObjectiveJSONToObject(text : String) : List<APIObjectives> {
        if(text.isNullOrBlank()) return mutableListOf()
        val gson = Gson()
        val arrayTutorialType = object : TypeToken<Array<APIObjectives>>() {}.type
        var apiObjectives: Array<APIObjectives> = gson.fromJson(text, arrayTutorialType)
        return apiObjectives.toList()
    }




}