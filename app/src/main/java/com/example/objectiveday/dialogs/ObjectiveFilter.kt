package com.example.objectiveday.dialogs

class ObjectiveFilter private constructor(
        var objectiveFilterType: ObjectiveFilterType? = null,
        var description: String? = null,
        var monday: Boolean?= null,
        var tuesday: Boolean? = null,
        var wednesday: Boolean? = null,
        var thursday: Boolean? = null,
        var friday: Boolean? = null,
        var saturday: Boolean? = null,
        var sunday: Boolean? = null
    ){

    data class Builder(
        var objectiveFilterType: ObjectiveFilterType = ObjectiveFilterType.NONE,
        var description : String? = null,
        var isMonday : Boolean? = null,
        var isTuesday : Boolean? = null,
        var isWednesday : Boolean? = null,
        var isThursday : Boolean? = null,
        var isFriday : Boolean? = null,
        var isSaturday : Boolean? = null,
        var isSunday : Boolean? = null
    ){
        //constructor()
        //constructor(objectiveFilterType: ObjectiveFilterType){
        //    this.objectiveFilterType = objectiveFilterType
        //}

        fun withFilterType(objectiveFilterType: ObjectiveFilterType){this.objectiveFilterType = objectiveFilterType}
        fun withDescriptionFilter(description: String?){this.description = description}
        fun withDaysFilters(
            monday:Boolean?,
            tuesday:Boolean?,
            wednesday:Boolean?,
            thurday:Boolean?,
            friday:Boolean?,
            saturday:Boolean?,
            sunday:Boolean?
        ){
            this.isMonday = monday
            this.isTuesday = tuesday
            this.isWednesday = wednesday
            this.isThursday = thurday
            this.isFriday = friday
            this.isSaturday = saturday
            this.isSunday = sunday
        }

        fun build() = ObjectiveFilter(objectiveFilterType, description, isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday)
    }


}