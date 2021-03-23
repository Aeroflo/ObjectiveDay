package com.example.objectiveday

import android.text.Editable
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

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

        fun stringToDateTime(string : String?) : LocalDateTime?{
            if(string.isNullOrBlank()) return null
            try {
                var localDateTime: LocalDateTime = LocalDateTime.parse(string)
                return localDateTime
            }catch(e: Exception){
                return null
            }
            return null
        }


        fun localDateTimeToString(localDateTime: LocalDateTime, pattern: String) : String{
            var dtf:DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
            return dtf.format(localDateTime)
        }

        fun getEmojiByUnicode(unicode: Int?): String? {
            if(unicode == null) return ""
            return String(Character.toChars(unicode))
        }
    }
}