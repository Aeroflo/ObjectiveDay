package com.example.objectiveday.internalData

import android.content.Context
import com.example.objectiveday.Utils
import com.example.objectiveday.dialogs.ObjectiveFilter
import com.example.objectiveday.dialogs.ObjectiveFilterType
import com.example.objectiveday.models.ObjectiveModel
import com.example.objectiveday.models.ObjectiveStatus
import com.example.objectiveday.webservices.apimodels.APIObjectives
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.objective_main_object_layout.view.*
import java.io.*
import java.nio.file.Files
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

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

    fun getDataFiltered(objectiveFilter: ObjectiveFilter?) : List<APIObjectives>{
        if(objectiveFilter == null) return getData()
        else{
            when(objectiveFilter!!.objectiveFilterType){
                ObjectiveFilterType.NONE -> return getData()
                ObjectiveFilterType.DESCRIPTION ->{
                    if(objectiveFilter.description.isNullOrBlank()) return getData()
                    else{
                        var listToReturn = mutableListOf<APIObjectives>()
                        existingObjectives.forEach{k, v ->
                            if(v.description!!.toLowerCase().contains(objectiveFilter!!.description!!.toLowerCase())) listToReturn.add(v)
                        }
                        newObjectives.forEach{k, v ->
                            if(v.description!!.toLowerCase().contains(objectiveFilter!!.description!!.toLowerCase())) listToReturn.add(v)
                        }
                        return listToReturn
                    }
                }
                ObjectiveFilterType.TODO -> {
                    var listToReturn = mutableListOf<APIObjectives>()
                    existingObjectives.forEach{k, v ->
                        if(v.toModel().getObjectiveStatus().equals(ObjectiveStatus.TODO)) listToReturn.add(v)
                    }
                    newObjectives.forEach{ k, v ->
                        if(v.toModel().getObjectiveStatus().equals(ObjectiveStatus.TODO)) listToReturn.add(v)
                    }
                    return listToReturn.toList()
                }
                ObjectiveFilterType.DONE -> {
                    var listToReturn = mutableListOf<APIObjectives>()
                    existingObjectives.forEach{k, v ->
                        if(v.toModel().getObjectiveStatus().equals(ObjectiveStatus.DONE)) listToReturn.add(v)
                    }
                    newObjectives.forEach{ k, v ->
                        if(v.toModel().getObjectiveStatus().equals(ObjectiveStatus.DONE)) listToReturn.add(v)
                    }
                    return listToReturn.toList()
                }
                ObjectiveFilterType.DAYS -> {
                    var listToReturn = mutableListOf<APIObjectives>()
                    if(!isFilterDayValid(objectiveFilter)) return listToReturn.toList()

                    existingObjectives.forEach{k, v ->
                        if(isObjectiveOnDay(objectiveFilter, v)) listToReturn.add(v)
                    }
                    newObjectives.forEach{ k, v ->
                        if(isObjectiveOnDay(objectiveFilter, v)) listToReturn.add(v)
                    }
                    return listToReturn.toList()

                }
            }
        }
        return getData()
    }

    private fun isFilterDayValid(objectiveFilter: ObjectiveFilter?) : Boolean{
        if(objectiveFilter == null) return false
        else{

            if(objectiveFilter.monday != null && objectiveFilter.monday!!) return true
            if(objectiveFilter.tuesday != null && objectiveFilter.tuesday!!) return true
            if(objectiveFilter.wednesday != null && objectiveFilter.wednesday!!) return true
            if(objectiveFilter.thursday != null && objectiveFilter.thursday!!) return true
            if(objectiveFilter.friday!= null && objectiveFilter.friday!!) return true
            if(objectiveFilter.saturday != null && objectiveFilter.saturday!!) return true
            if(objectiveFilter.sunday != null && objectiveFilter.sunday!!) return true
        }
        return false
    }

    private fun isObjectiveOnDay(objectiveFilter: ObjectiveFilter?, apiObjectives: APIObjectives) :Boolean{
        if(objectiveFilter == null) return false
        else{
            if(objectiveFilter.monday != null && objectiveFilter.monday!! && apiObjectives.monday!= null && apiObjectives.monday!!) return true
            if(objectiveFilter.tuesday != null && objectiveFilter.tuesday!! && apiObjectives.tuesday!= null && apiObjectives.tuesday!!) return true
            if(objectiveFilter.wednesday != null && objectiveFilter.wednesday!! && apiObjectives.wednesday!= null && apiObjectives.wednesday!!) return true
            if(objectiveFilter.thursday != null && objectiveFilter.thursday!! && apiObjectives.thurday!= null && apiObjectives.thurday!!) return true
            if(objectiveFilter.friday != null && objectiveFilter.friday!! && apiObjectives.friday!= null && apiObjectives.friday!!) return true
            if(objectiveFilter.saturday != null && objectiveFilter.saturday!! && apiObjectives.saturday!= null && apiObjectives.saturday!!) return true
            if(objectiveFilter.sunday != null && objectiveFilter.sunday!! && apiObjectives.sunday!= null && apiObjectives.sunday!!) return true
        }
        return false
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

    fun getObjectiveToNotify() : List<APIObjectives>{
        var listToReturn = mutableListOf<APIObjectives>()

        listToReturn.addAll(getObjectiveTodo(LocalDateTime.now(), true, existingObjectives.values.filter { o -> o.notify != null && o.notify }.toList()))
        listToReturn.addAll(getObjectiveTodo(LocalDateTime.now(), true, newObjectives.values.filter { o -> o.notify != null && o.notify }.toList()))

        return listToReturn
    }

    private fun getObjectiveTodo(date : LocalDateTime, checkTime : Boolean, apiObjectives: List<APIObjectives>) : List<APIObjectives>{
        var listToReturn = mutableListOf<APIObjectives>()
        if(apiObjectives.isNullOrEmpty()) return listToReturn
        var dayOfWeek = date.dayOfWeek

        apiObjectives.forEach{o ->
            when (dayOfWeek) {
                DayOfWeek.MONDAY -> if (o.monday != null && o.monday && isObjectiveTodoOnTime(date, o, checkTime)) listToReturn.add(o)
                DayOfWeek.TUESDAY -> if (o.tuesday != null && o.tuesday && isObjectiveTodoOnTime(date, o, checkTime)) listToReturn.add(o)
                DayOfWeek.WEDNESDAY -> if (o.wednesday != null && o.wednesday && isObjectiveTodoOnTime(date, o,checkTime)) listToReturn.add(o)
                DayOfWeek.THURSDAY -> if (o.thurday != null && o.thurday && isObjectiveTodoOnTime(date, o,checkTime)) listToReturn.add(o)
                DayOfWeek.FRIDAY -> if (o.friday != null && o.friday && isObjectiveTodoOnTime(date, o,checkTime)) listToReturn.add(o)
                DayOfWeek.SATURDAY -> if (o.saturday != null && o.saturday && isObjectiveTodoOnTime(date, o,checkTime)) listToReturn.add(o)
                DayOfWeek.SUNDAY -> if (o.sunday != null && o.sunday) listToReturn.add(o)
                }
        }

        return listToReturn
    }

    private fun isObjectiveTodoOnTime(date : LocalDateTime, apiObjectives: APIObjectives, checkTime : Boolean) : Boolean{
        var dayStart = date.withHour(0).withMinute(0).withSecond(0).withNano(0)
        var lastDone : LocalDateTime? = Utils.stringToDateTime(apiObjectives.lastDoneDate)
        var needToBeDoneOnTheDay = lastDone == null || lastDone!!.isBefore(dayStart)
        if(!apiObjectives.time.isNullOrBlank() && checkTime){
            var localTime = Utils.stringToTime(apiObjectives.time)
            if(localTime == null) localTime = LocalTime.now().withHour(0).withMinute(0)
            var plusHour = date.toLocalTime().plusHours(1)
            return needToBeDoneOnTheDay && localTime!!.isBefore(plusHour)
        }else return needToBeDoneOnTheDay
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