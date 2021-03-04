package com.example.objectiveday.models

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.Bindable
import androidx.databinding.InverseMethod
import com.example.objectiveday.Utils
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

import kotlin.math.roundToInt

class ObjectiveModel  private constructor (
    var id : Long? = null,
    var parentId : Long? = null,
    var description : String = "",
    var dayChecker : Int = 0,
    var time : LocalTime? = null ,
    var isActive : Boolean = true,
    var isNotifiable : Boolean = false,
    var nbTriggered : Int = 0,
    var nbDone : Int = 0,
    var children : Array<ObjectiveModel>? = null
) : Serializable {

    data class Builder(
        var id : Long? = null,
        var parentId : Long? = null,
        var description : String = "",
        var dayChecker : Int = 0,
        var time : LocalTime? = null ,
        var isActive : Boolean = true,
        var nbTriggered : Int = 0,
        var nbDone : Int = 0,
        var isNotifiable : Boolean = false,
        var children : Array<ObjectiveModel>? = null
    ){

        fun withId(id : Long?) = apply { this.id = id }
        fun withParentId(parentId: Long?) =  apply {this.parentId = parentId}
        fun withDescription(description: String) = apply { this.description = description }
        fun withDayChecker(dayChecker: Int)  = apply{this.dayChecker = dayChecker}
        fun withTime(time: LocalTime?) = apply { this.time = time }
        fun withIsActive(isActive: Boolean) = apply { this.isActive = isActive }
        fun withIsNotifiable(isNotifiable: Boolean ) = apply{this.isNotifiable = isNotifiable}
        fun withNbTriggered(nbTriggered: Int) = apply { this.nbTriggered = nbTriggered }
        fun withNbDone(nbDone: Int) = apply { this.nbDone = nbDone }
        fun withChildren(children: Array<ObjectiveModel>) = apply { this.children = children }

        fun build() = ObjectiveModel(id, parentId, description, dayChecker, time, isActive,  isNotifiable, nbTriggered, nbDone, children)
    }

    fun integerToBinary(value: Int, index : Int) : Boolean{
        return if(index < 0 || index > 7 || value < 0 || value > 127) false
        else {
            val r : String = String.format("%7s", Integer.toBinaryString(value))
            if(r.length <= index) return false
            return r.codePointAt(index).equals(49) //49 is char "1"
        }
    }

    var isMonday : Boolean = integerToBinary(dayChecker, 0)
    var isTuesday : Boolean = integerToBinary(dayChecker, 1)
    var isWednesday : Boolean = integerToBinary(dayChecker, 2)
    var isThursday : Boolean = integerToBinary(dayChecker, 3)
    var isFriday : Boolean = integerToBinary(dayChecker, 4)
    var isSaturday : Boolean = integerToBinary(dayChecker, 5)
    var isSunday : Boolean = integerToBinary(dayChecker, 6)

    fun validateDays( dayChecker: Int){
        this.dayChecker = dayChecker
        isMonday = integerToBinary(dayChecker, 0)
        isTuesday = integerToBinary(dayChecker, 1)
        isWednesday = integerToBinary(dayChecker, 2)
        isThursday = integerToBinary(dayChecker, 3)
        isFriday = integerToBinary(dayChecker, 4)
        isSaturday =integerToBinary(dayChecker, 5)
        isSunday= integerToBinary(dayChecker, 6)
    }

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString().toString(),
        parcel.readInt(),
        TODO("time"),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArray(CREATOR)
    )


    fun calculatePercentageDone() : Int{
        return if(this.nbTriggered == 0) 0
        else{
            var calculated : Int = ((nbDone.toDouble() / nbTriggered.toDouble()) * 100).roundToInt()
            System.out.println(""+nbDone+" "+nbTriggered+"    "+calculated)
            return calculated
        }
    }

    fun getDaysText() : String{
        if(isAllDays()) return "Every day"
        else if(isWeekDaysOnly()) return "Week days only"
        else if(isWeekEndOnly()) return "Week end only"
        else if(noDaysDefined()) return "No Days defined"
        else{
            var toReturn : String= ""
            if(isMonday) toReturn += "Mon, "
            if(isTuesday) toReturn += "Tue, "
            if(isWednesday) toReturn +="Wed, "
            if(isThursday) toReturn +="Thu, "
            if(isFriday) toReturn +="Fri, "
            if(isSaturday) toReturn +="Sat, "
            if(isSunday) toReturn +="Sun, "
            return toReturn
        }

    }

    fun isAllDays(): Boolean{
        return (isMonday &&
                isTuesday &&
                isWednesday &&
                isThursday &&
                isFriday &&
                isSaturday &&
                isSunday)
    }

    fun noDaysDefined() : Boolean {
        return !(isMonday || isTuesday || isWednesday || isThursday || isFriday || isSaturday || isSunday)
    }

    fun isWeekEndOnly() : Boolean {
        return isSaturday && isSunday &&
                !(isMonday || isTuesday || isWednesday || isThursday || isFriday)
    }

    fun isWeekDaysOnly() : Boolean{
        return isMonday && isTuesday && isWednesday && isThursday && isFriday &&
                !(isSaturday || isSunday)
    }

    var isWeekEnd : Boolean? = null
    fun isWeekEndSet() : Boolean {
        isWeekEnd = isSaturday && isSunday
        return isWeekEnd as Boolean
    }

    fun isWeekDaySet() : Boolean {return isMonday && isTuesday && isWednesday && isThursday && isFriday}

    companion object CREATOR : Parcelable.Creator<ObjectiveModel> {
        override fun createFromParcel(parcel: Parcel): ObjectiveModel {
            return ObjectiveModel(parcel)
        }

        override fun newArray(size: Int): Array<ObjectiveModel?> {
            return arrayOfNulls(size)
        }
    }


    fun getNextDateRec(recurrence: Int) : String{
        val nextDates : Set<LocalDateTime> = getNextDateList(false) ?: return "NONE"
        if(nextDates.isEmpty()) return "NONE"

        if(recurrence > nextDates.size) return "NONE"


        val dateFormat: String = Utils.localDateTimeToString(nextDates.take(3).get(recurrence), "E dd MMM yyyy '@'HH:mm")
        return dateFormat
    }

    var nextDates : Set<LocalDateTime>? = null
    fun getNextDateList(reset : Boolean) : Set<LocalDateTime>? {
        //if(this.dayChecker == 0) return null
        if(!reset && this.nextDates != null) return this.nextDates


        var time : LocalTime = if(this.time != null) this.time!! else LocalTime.of(0, 0)
        var currentDateTime : LocalDateTime = LocalDateTime.now().withHour(time.hour).withMinute(time.minute)
        val nextDates = sortedSetOf<LocalDateTime>()
        var occurencesWeek : Int = 3
        var currentWeek : Int = 0
        while(nextDates.size < 3 && currentWeek < occurencesWeek ){
            if(isMonday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)))
            if(isTuesday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)))
            if(isWednesday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY)))
            if(isThursday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.THURSDAY)))
            if(isFriday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)))
            if(isSaturday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)))
            if(isSunday) nextDates.add(currentDateTime.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)))

            currentDateTime = currentDateTime.plusWeeks(1)
            currentWeek = currentWeek+1
        }

        //sort
        System.out.println(nextDates.toString())
        this.nextDates = nextDates
        return this.nextDates
    }
}