package com.example.objectiveday.controllers

import android.view.View
import android.widget.CheckBox
import android.widget.Switch

class ObjectiveWeekController private constructor(
    val mondayChk: CheckBox?,
    val tuesdayChk: CheckBox?,
    val wednesdayChk: CheckBox?,
    val thurdayChk: CheckBox?,
    val fridayChk: CheckBox?,
    val saturdayChk: CheckBox?,
    val sundayChk : CheckBox?,
    val weekDaysSw : Switch?,
    val weekEndSw : Switch?) {

    data class Builder(
        var mondayChk: CheckBox? = null,
        var tuesdayChk: CheckBox? = null,
        var wednesdayChk: CheckBox? = null,
        var thurdayChk: CheckBox? = null,
        var fridayChk: CheckBox? = null,
        var saturdayChk: CheckBox? = null,
        var sundayChk : CheckBox? = null,
        var weekDaySw : Switch? = null,
        var weekEndSw : Switch? = null) {

        fun withMondayChk(mondayChk: CheckBox?) = apply { this.mondayChk = mondayChk }
        fun withTuesdayChk(tuesdayChk: CheckBox?) = apply { this.tuesdayChk = tuesdayChk }
        fun withWednesdayChk(wednesdayChk: CheckBox?) = apply { this.wednesdayChk = wednesdayChk }
        fun withThurdayChk(thurdayChk: CheckBox?) = apply { this.thurdayChk = thurdayChk }
        fun withFridayChk(fridayChk: CheckBox?) = apply { this.fridayChk = fridayChk }
        fun withSaturdayChk(saturdayChk: CheckBox?) = apply { this.saturdayChk = saturdayChk }
        fun withSundayChk(sundayChk: CheckBox?) = apply { this.sundayChk = sundayChk }

        fun withWeekDaysSW(weekDaySw: Switch?) = apply{this.weekDaySw = weekDaySw}
        fun withWeekEndSW(weekEndSw: Switch?) = apply{this.weekEndSw = weekEndSw}

        fun build() = ObjectiveWeekController(
            mondayChk, tuesdayChk, wednesdayChk, thurdayChk, fridayChk, saturdayChk, sundayChk,
            weekDaySw, weekEndSw
        )
    }

    //Set all action listeners
    fun setCheckBoxesActionListeners(){
        //Set action listeners:
        mondayChk?.setOnClickListener {updateWeekFromCheckBox() }
        thurdayChk?.setOnClickListener{ updateWeekFromCheckBox() }
        wednesdayChk?.setOnClickListener{ updateWeekFromCheckBox() }
        thurdayChk?.setOnClickListener{ updateWeekFromCheckBox() }
        fridayChk?.setOnClickListener{ updateWeekFromCheckBox() }

        saturdayChk?.setOnClickListener{updateWeekFromCheckBox() }
        sundayChk?.setOnClickListener{ updateWeekFromCheckBox() }

        weekDaysSw?.setOnClickListener(View.OnClickListener { updateWeekDaysFromSwitch(weekDaysSw.isChecked) })
        weekEndSw?.setOnClickListener(View.OnClickListener { updateWeedEndFromSwitch(weekEndSw.isChecked) })
    }

    //fun which update weekDay
    fun updateWeekFromCheckBox(){
        this.weekDaysSw?.isChecked = areAllWeekDaysChecked()
        this.weekEndSw?.isChecked = areAllWeekEndChecked()
    }

    fun updateWeekDaysFromSwitch(isChecked :Boolean){
        if(isChecked){
            this.mondayChk?.isChecked = true;
            this.tuesdayChk?.isChecked = true;
            this.wednesdayChk?.isChecked = true;
            this.thurdayChk?.isChecked = true;
            this.fridayChk?.isChecked = true;
        }else{
            this.mondayChk?.isChecked = false;
            this.tuesdayChk?.isChecked = false;
            this.wednesdayChk?.isChecked = false;
            this.thurdayChk?.isChecked = false;
            this.fridayChk?.isChecked = false;
        }
    }

    fun updateWeedEndFromSwitch(isChecked: Boolean){
        if(isChecked){
            this.saturdayChk?.isChecked = true
            this.sundayChk?.isChecked = true
        }
        else{
            this.saturdayChk?.isChecked = false
            this.sundayChk?.isChecked = false
        }
    }

    fun areAllWeekDaysChecked() : Boolean{
        var toReturn = true;
        if(this.mondayChk != null) toReturn = toReturn && this.mondayChk.isChecked
        if(this.tuesdayChk != null) toReturn = toReturn && this.tuesdayChk.isChecked
        if(this.wednesdayChk != null) toReturn = toReturn && this.wednesdayChk.isChecked
        if(this.thurdayChk != null) toReturn = toReturn && this.thurdayChk.isChecked
        if(this.fridayChk != null) toReturn = toReturn && this.fridayChk.isChecked
        return toReturn
    }

    fun areAllWeekEndChecked() : Boolean{
        var toReturn = true;
        if(this.saturdayChk != null) toReturn = toReturn && this.saturdayChk.isChecked
        if(this.sundayChk != null) toReturn = toReturn && this.sundayChk.isChecked
        return toReturn
    }

    //fun which return an number
    fun getAllDays() : String{
        return Integer.toHexString(getAllDaysInteger())
    }

    fun getAllDaysInteger() : Int{
        var valueToReturn : Int = 0;
        if(sundayChk?.isChecked!!) valueToReturn += 1
        if(saturdayChk?.isChecked!!) valueToReturn += 2
        if(fridayChk?.isChecked!!) valueToReturn += 4
        if(thurdayChk?.isChecked!!) valueToReturn += 8
        if(wednesdayChk?.isChecked!!) valueToReturn +=16
        if(tuesdayChk?.isChecked!!) valueToReturn +=32
        if(mondayChk?.isChecked!!) valueToReturn +=64
        return valueToReturn
    }

}
