package com.example.objectiveday

import android.text.Editable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Utils {
    companion object {
        fun timeToString(time: LocalTime?): String {
            if (time != null) {
                val timeText: String = time.format(DateTimeFormatter.ofPattern("hh:mm"))
                return timeText
            }
            return "";
        }

        fun stringToTime(string: String?): LocalTime?{
            try {
                var localTime: LocalTime = LocalTime.parse(string)
                return localTime
            }catch(e: Exception){
                return null
            }
            return null
        }
    }
}