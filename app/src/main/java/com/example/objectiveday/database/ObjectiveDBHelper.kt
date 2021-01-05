package com.example.objectiveday.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.objectiveday.Utils
import com.example.objectiveday.models.ObjectiveModel
import java.lang.Exception
import java.time.LocalTime

class ObjectiveDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(db != null){
            db.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }
    }

    private var QUERY_GET_ALL_BASE : String = "Select o.* from "+DBContract.ObjectiveEntry.TABLE_NAME+" o "

    @Throws(SQLiteConstraintException::class)
    fun getAllObjective() : List<ObjectiveModel>{
        val db = writableDatabase
        var queryBase : String = QUERY_GET_ALL_BASE

        var listOfObjectiveToReturn : ArrayList<ObjectiveModel> = ArrayList()

        val cursor : Cursor
        try {
            cursor = db.rawQuery(queryBase, null)
            while(cursor.moveToNext()){
                var id : Long = cursor.getLong(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_ID))
                var description : String = cursor.getString(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_DESCRIPTION))
                var days : Int = cursor.getInt(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_DAY))
                var timeString : String? = cursor.getString(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_TIME))
                var time : LocalTime? = Utils.stringToTime(timeString)
                var isNotifiableInt : Int? = cursor.getInt(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_NOTIFY))
                var isNotifiable : Boolean = false
                if(isNotifiableInt != null){
                    when(isNotifiableInt){
                        1 -> isNotifiable = true
                    }
                }


                var objectiveModel : ObjectiveModel = ObjectiveModel.Builder()
                    .withId(id)
                    .withDescription(description)
                    .withDayChecker(days)
                    .withTime(time)
                    .withIsNotifiable(isNotifiable)
                    .build()
                listOfObjectiveToReturn.add(objectiveModel)
            }
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList<ObjectiveModel>()
        }

        return listOfObjectiveToReturn
    }

    @Throws(SQLiteConstraintException::class)
    fun getAllObjectiveFiltered(filters : String?) : List<ObjectiveModel>?{
        if(filters == null || filters.isEmpty()) return getAllObjective()

        val db = writableDatabase //should be singleton?
        var queryBase : String = QUERY_GET_ALL_BASE+filters

        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(queryBase, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList<ObjectiveModel>()
        }

        var listOfObjectiveToReturn : List<ObjectiveModel> =  ArrayList<ObjectiveModel>()
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                //build objective model
                var objectiveModel : ObjectiveModel = ObjectiveModel.Builder()
                    .build()
                cursor.moveToNext()
            }
        }
        return listOfObjectiveToReturn
    }

    fun saveObjective(objectiveModel : ObjectiveModel) : Boolean{
        if(objectiveModel.id == null){
            return createNewObjective(objectiveModel)
        }
        else{
            var foundObjectiveModel : ObjectiveModel? = findObjectiveById(objectiveModel.id!!)
            if(objectiveModel == null) return createNewObjective(objectiveModel)
            else return updateObjective(objectiveModel)
        }
    }

    private fun createNewObjective(objectiveModel : ObjectiveModel) : Boolean {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_DESCRIPTION, objectiveModel.description)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_DAY, objectiveModel.dayChecker)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_NB_DONE, 0)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_NB_TRIGGERED, 0)

            var timeString : String = Utils.timeToString(objectiveModel.time)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_TIME, timeString)

            var isNotifiableValue : Int = if(objectiveModel.isNotifiable) 1 else 0
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_NOTIFY, isNotifiableValue)

            db.insert(DBContract.ObjectiveEntry.TABLE_NAME, null, contentValues)
        } catch(e:Exception){
            return false
        }
        return true
    }

    private fun updateObjective(objectiveModel: ObjectiveModel) : Boolean{
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_DESCRIPTION, objectiveModel.description)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_DAY, objectiveModel.dayChecker)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_NB_DONE, objectiveModel.nbDone)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_NB_TRIGGERED, objectiveModel.nbTriggered)

            var timeString : String = Utils.timeToString(objectiveModel.time)
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_TIME, timeString)

            var isNotifiableValue : Int = if(objectiveModel.isNotifiable) 1 else 0
            contentValues.put(DBContract.ObjectiveEntry.COLUMN_NOTIFY, isNotifiableValue)

            db.update(DBContract.ObjectiveEntry.TABLE_NAME,  contentValues, DBContract.ObjectiveEntry.COLUMN_ID+"=?", arrayOf(objectiveModel.id.toString()))
        } catch(e:Exception){
            return false
        }
        return true
    }

    fun findObjectiveById(id : Long) : ObjectiveModel?{
        var objectiveModelToReturn : ObjectiveModel? = null
        val db = this.readableDatabase
        val selectQuery = QUERY_GET_ALL_BASE +" WHERE " + DBContract.ObjectiveEntry.COLUMN_ID + " = '" + id + "'"
        val cursor = db.rawQuery(selectQuery, null)
        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {

                    var id : Long = cursor.getLong(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_ID))
                    var description : String = cursor.getString(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_DESCRIPTION))
                    var days : Int = cursor.getInt(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_DAY))
                    var time : String? = cursor.getString(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_TIME))
                    var localTime : LocalTime? = Utils.stringToTime(time)
                    var nbDone : Int? = cursor.getInt(cursor.getColumnIndexOrThrow(DBContract.ObjectiveEntry.COLUMN_NB_DONE))
                    var nbTriggered : Int? = cursor.getInt(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_NB_TRIGGERED))
                    var isNotifiableInt : Int? = cursor.getInt(cursor.getColumnIndex(DBContract.ObjectiveEntry.COLUMN_NOTIFY))

                    val objectiveBuilder : ObjectiveModel.Builder = ObjectiveModel.Builder()
                    objectiveBuilder.withId(id)
                    if(description != null) objectiveBuilder.withDescription(description)
                    if(days != null) objectiveBuilder.withDayChecker(days)
                    if(nbDone != null) objectiveBuilder.withNbDone(nbDone)
                    if(nbTriggered != null) objectiveBuilder.withNbTriggered(nbTriggered)
                    if(localTime != null) objectiveBuilder.withTime(localTime)
                    if(isNotifiableInt != null) {
                        when (isNotifiableInt) {
                            0 -> objectiveBuilder.withIsNotifiable(false)
                            1 -> objectiveBuilder.withIsNotifiable(true)
                            else -> objectiveBuilder.withIsNotifiable(false)
                        }
                    }
                    else objectiveBuilder.withIsNotifiable(false)

                    objectiveModelToReturn = objectiveBuilder.build()
                }
            }
        } finally {
            cursor.close()
        }
        return objectiveModelToReturn
    }





    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 2
        val DATABASE_NAME = "objectiveDatabase.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.ObjectiveEntry.TABLE_NAME + " (" +
                    DBContract.ObjectiveEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    DBContract.ObjectiveEntry.COLUMN_DESCRIPTION + " TEXT," +
                    DBContract.ObjectiveEntry.COLUMN_DAY + " INTEGER," +
                    DBContract.ObjectiveEntry.COLUMN_TIME + " TEXT," +
                    DBContract.ObjectiveEntry.COLUMN_NB_DONE + " INTEGER," +
                    DBContract.ObjectiveEntry.COLUMN_NB_TRIGGERED + " INTEGER, " +
                    DBContract.ObjectiveEntry.COLUMN_NOTIFY + " INTEGER)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.ObjectiveEntry.TABLE_NAME
    }

}