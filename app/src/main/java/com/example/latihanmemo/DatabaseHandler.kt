package com.example.latihanmemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class DatabaseHandler (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Databasesql"

        private val TABLE_ACTIVITY = "Recordtable"

        private val KEY_ID ="_id"
        private val KEY_CALENDAR ="calendar"
        private val KEY_KETERANGAN ="keterangan"

    }
    //sqlite open helper harus mempunyai oncreate dan onupgrade
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE =("CREATE TABLE " + TABLE_ACTIVITY +"("
                +KEY_ID+" INTEGER PRIMARY KEY,"
                +KEY_CALENDAR+" TEXT,"
                + KEY_KETERANGAN+" TEXT)")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITY")
        onCreate(db)
    }
    fun addActivity (act: modelclass) : Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CALENDAR,act.calendar)
        contentValues.put(KEY_KETERANGAN,act.keterangan)

        val success = db.insert(TABLE_ACTIVITY,null,contentValues)
        db.close()
        return success
    }
    fun viewActivity(): ArrayList<modelclass> {
        val actList = ArrayList<modelclass>()
        val selectQuery = "SELECT * FROM ${TABLE_ACTIVITY}"

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var calendar: String
        var keterangan: String


        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                calendar = cursor.getString(cursor.getColumnIndex(KEY_CALENDAR))
                keterangan = cursor.getString(cursor.getColumnIndex(KEY_KETERANGAN))

                val act = modelclass(id = id, calendar = calendar, keterangan = keterangan)
                actList.add(act)
            } while (cursor.moveToNext())
        }
        return actList
    }
    fun deleteActivity(act:modelclass): Int {
        val db = this.writableDatabase
        val contenValues = ContentValues()
        contenValues.put(KEY_ID, act.id)

        val success =db.delete(TABLE_ACTIVITY, KEY_ID + "=" + act.id,null )
        db.close()
        return success
    }
    fun updateActivity(act: modelclass):Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_CALENDAR,act.calendar)
        contentValues.put(KEY_KETERANGAN,act.keterangan)
         val success = db.update(TABLE_ACTIVITY, contentValues, KEY_ID + "=" +act.id,null)
        db.close()
        return success
    }

}