package com.example.objectiveday.database

import android.provider.BaseColumns

object DBContract{

    class ObjectiveEntry : BaseColumns{
        companion object{
            val TABLE_NAME = "objective"
            val COLUMN_ID = "id"
            val COLUMN_DAY = "days"
            val COLUMN_TIME = "time"
            val COLUMN_DESCRIPTION = "description"
            val COLUMN_NB_DONE = "nb_done"
            val COLUMN_NB_TRIGGERED = "nb_triggered"
            val COLUMN_NOTIFY = "notify"
        }
    }
}