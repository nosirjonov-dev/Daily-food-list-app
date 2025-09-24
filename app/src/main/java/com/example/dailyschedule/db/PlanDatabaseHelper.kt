package com.example.dailyschedule.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dailyschedule.models.DateModel
import com.example.dailyschedule.models.TaskModel

class PlanDatabaseHelper(context: Context) : SQLiteOpenHelper(context, plan_db, null, version) {

    companion object{
        const val plan_db = "plan_db"
        const val version = 1

        const val dates_table = "dates"
        const val dates_id = "id"
        const val dates_date = "date"
        const val dates_day = "day"

        const val tasks_table = "tasks"
        const val tasks_id = "id"
        const val tasks_date_id = "dateId"
        const val tasks_title = "title"
        const val tasks_time = "time"
        const val tasks_image = "image"

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
    CREATE TABLE $dates_table (
        $dates_id INTEGER PRIMARY KEY AUTOINCREMENT,
        $dates_date TEXT,
        $dates_day TEXT
    )
    """.trimIndent()
        )

        db.execSQL(
            """
    CREATE TABLE $tasks_table (
        $tasks_id INTEGER PRIMARY KEY AUTOINCREMENT,
        $tasks_date_id INTEGER,
        $tasks_title TEXT,
        $tasks_time TEXT,
        $tasks_image TEXT,
        FOREIGN KEY($tasks_date_id) REFERENCES $dates_table($dates_id)
    )
    """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $dates_table")
        db.execSQL("DROP TABLE IF EXISTS $tasks_table")
        onCreate(db)
    }

    // Sana qo‘shish
    fun insertDate(dateModel: DateModel) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(dates_date, dateModel.date)
            put(dates_day, dateModel.day)
        }
        db.insert(dates_table, null, cv)
    }

    fun getAllDates(): List<DateModel> {
        val list = mutableListOf<DateModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $dates_table", null)
        if (cursor.moveToFirst()) {
            do {
                val model = DateModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(dates_id)),
                    cursor.getString(cursor.getColumnIndexOrThrow(dates_date)),
                    cursor.getString(cursor.getColumnIndexOrThrow(dates_day))
                )
                list.add(model)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateDate(date: DateModel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(dates_date, date.date)
            put(dates_day, date.day)
        }
        return db.update(dates_table, values, "$dates_id = ?", arrayOf(date.id.toString()))
    }


    fun insertTask(task: TaskModel) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(tasks_date_id, task.dateId)
            put(tasks_title, task.title)
            put(tasks_time, task.time)
            put(tasks_image, task.image)
        }
        db.insert(tasks_table, null, cv)
    }

    fun getTasksByDate(dateId: Int): List<TaskModel> {
        val list = mutableListOf<TaskModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tasks_table WHERE $tasks_date_id = ?", arrayOf(dateId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val task = TaskModel(
                    cursor.getInt(cursor.getColumnIndexOrThrow(tasks_id)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(tasks_date_id)),
                    cursor.getString(cursor.getColumnIndexOrThrow(tasks_title)),
                    cursor.getString(cursor.getColumnIndexOrThrow(tasks_time)),
                    cursor.getString(cursor.getColumnIndexOrThrow(tasks_image))
                )
                list.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateTask(task: TaskModel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(tasks_title, task.title)
            put(tasks_time, task.time)
            put(tasks_image, task.image)
        }
        return db.update(tasks_table, values, "$tasks_id = ?", arrayOf(task.id.toString()))
    }

    fun deleteTask(taskId: Int): Int {
        val db = writableDatabase
        return db.delete(tasks_table, "$tasks_id = ?", arrayOf(taskId.toString()))
    }
}
