package com.example.objectiveday.webservices.apimodels

import com.example.objectiveday.Utils
import com.example.objectiveday.models.ObjectiveModel
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class APIObjectives (
    @SerializedName("id") val id : Long? = null,
    @SerializedName("parent_id") val parent_id : Long? = null,
    @SerializedName("description") val description : String? = null,
    @SerializedName("monday") val monday : Boolean? = null,
    @SerializedName("tuesday") val tuesday : Boolean? = null,
    @SerializedName("wednesday") val wednesday : Boolean? = null,
    @SerializedName("thursday") val thurday : Boolean? = null,
    @SerializedName("friday") val friday : Boolean? = null,
    @SerializedName("saturday") val saturday : Boolean? = null,
    @SerializedName("sunday") val sunday : Boolean? = null,
    @SerializedName("time") val time : String? = null,
    @SerializedName("notify") val notify : Boolean? = null,
    @SerializedName("last_notified_date") val lastNotifiedDate : String? = null,
    @SerializedName("last_time_modified") val lastModifiedDate : String? = null,
    @SerializedName("next_objective_date") val nextObjectiveDays : List<String>? = null,
    @SerializedName("is_active") val isActive : Boolean? = null,
    @SerializedName("children_objectives") val childrenObjective : List<APIObjectives>? = null,
    @SerializedName("last_done_date") val lastDoneDate : String? = null
){
    fun toModel() : ObjectiveModel{
        var childrenObjective = ArrayList<ObjectiveModel>()
        if(this.childrenObjective!= null) this.childrenObjective.forEach{child -> childrenObjective.add(child.toModel())}
        var objectiveModelBuilder =  ObjectiveModel.Builder()
            .withId(id)
            .withParentId(parent_id)
            .withDayChecker(parseDays())
            .withDescription(if(description !=null) description else "")
            //.withIsActive()
            .withIsNotifiable(if(notify !=null) notify else false)
            .withChildren(childrenObjective.toTypedArray())
        if(lastDoneDate != null){
            objectiveModelBuilder.withLastDoneTime(Utils.stringToDateTime(lastDoneDate))
        }
        return objectiveModelBuilder.build()

    }

    fun parseDays() : Int{
        var days : Int = 0
        if(monday != null && monday.equals(true)) days = days + 64
        if(tuesday != null && tuesday.equals(true)) days = days + 32
        if(wednesday != null && wednesday.equals(true)) days = days + 16
        if(thurday != null && thurday.equals(true)) days = days + 8
        if(friday != null && friday.equals(true)) days = days + 4
        if(saturday != null && saturday.equals(true)) days = days + 2
        if(sunday != null && sunday.equals(true)) days = days + 1
        return days
    }
}


