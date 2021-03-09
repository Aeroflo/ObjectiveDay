package com.example.objectiveday.adapters

import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.exp
import kotlin.math.roundToInt

object BindingAdapter {

    @BindingAdapter("percentage")
    @JvmStatic
    fun bindPercentage(view: TextView, amount: Int) {
        var text : String = formatPercentageText(amount)
        view.text = formatPercentageText(amount)//amount.toString()+"%"
        view.setBackgroundColor(calculateTextPercentColor(amount))
    }

    fun calculateTextPercentColor(percentage : Int) : Int {
        val redValue = 255 - (exp(percentage.toDouble()) * (255.toDouble()/ exp(100.toDouble()))).roundToInt()
        val greenValue = (percentage.toDouble() * (255.toDouble()/100.toDouble())).roundToInt()
        if(redValue >= 0 && redValue <= 255 && greenValue >= 0 && greenValue <= 255)
            return Color.rgb(redValue, greenValue, 0)
        else
            return Color.rgb(255, 255, 255)
    }

    fun formatPercentageText(percentage: Int) : String{
        var text = "";
        var nbDigit = 3 - countNbDigitsInInt(percentage)
        for(i in 0..nbDigit) text = text+" "
        text=text+percentage.toString()+"%"
        return text
    }

    fun countNbDigitsInInt(value : Int): Int{
        var count : Int = 0
        var num : Int = value
        while(num != 0) {
            num /= 10
            ++count
        }
        return count
    }

    @BindingAdapter("daysText")
    @JvmStatic
    fun bindDayTest(view: TextView, dayText: String) {
        view.text = dayText
    }


    @BindingAdapter("dayCheckBox")
    @JvmStatic
    fun bindDayBoolean(view : CheckBox, bool : Boolean){
        view.isChecked = bool
    }

    @BindingAdapter("notifySwitch")
    @JvmStatic
    fun bindNotifySwitch(view : Switch, bool : Boolean){
        view.isChecked = bool
    }

    @BindingAdapter("weekPartSwitch")
    @JvmStatic
    fun bindWeekPartBoolean(view : Switch, bool : Boolean){
        view.isChecked = bool
    }

    @BindingAdapter("nbTriggered")
    @JvmStatic
    fun bindValueNbTriggered(view : TextView,  value : Int){
        var text : String = "Number of time triggered: $value"
        view.text = text
    }

    @BindingAdapter("nbDone")
    @JvmStatic
    fun bindValueNbDone(view : TextView,  value : Int){
        var text : String = "Number of time done: $value"
        view.text = text
    }

    @BindingAdapter("achivementSeparator")
    @JvmStatic
    fun bindViewAchievementSeparator(view : View, amount : Int){
        view.setBackgroundColor(calculateTextPercentColor(amount))
    }

    @BindingAdapter("time")
    @JvmStatic
    fun bindTime(view: TextView,  time: LocalTime?){
        if(time != null){
            var timeText : String = time.format(DateTimeFormatter.ofPattern("hh:mm"))
            view.text = timeText
        }
        else view.text = "hh:mm"
    }

    @BindingAdapter("dateString")
    @JvmStatic
    fun bindDateString(view : TextView, date :String){
        view.text = date
    }


}