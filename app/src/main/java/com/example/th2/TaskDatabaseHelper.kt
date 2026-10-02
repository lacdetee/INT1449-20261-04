package com.example.th2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DEADLINE = "deadline"
        private const val COLUMN_IS_COMPLETED = "is_completed"
        private const val COLUMN_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DEADLINE TEXT NOT NULL,
                $COLUMN_NOTE TEXT DEFAULT '',
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun addTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_DEADLINE, task.deadline)
            put(COLUMN_NOTE, task.note)
            put(COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        }
        val result = db.insert(TABLE_TASKS, null, values)
        db.close()
        return result
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TASKS ORDER BY $COLUMN_DEADLINE ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)),
                    note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
                )
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tasks
    }

    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }

    fun deleteTask(taskId: Int) {
        val db = writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
        db.close()
    }
}
