package com.ain.blankcanvas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class TaskDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "taskmanager.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_PARENT_ID = "parent_id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PARENT_ID INTEGER,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT
            )
        """.trimIndent()
        db.execSQL(createTableSQL)
        insertDefaultTasks(db)
    }

    private fun insertDefaultTasks(db: SQLiteDatabase) {
        val defaultTasks = listOf(
            Triple("Welcome Task", "This is your first task", ItemType.GROUP),
            Triple("Subtask Example", "This is a subtask", ItemType.TASK),
            Triple("Another Task", "Feel free to edit", ItemType.TASK)
        )

        defaultTasks.forEach { (title, desc, type) ->
            val values = ContentValues().apply {
                put(COLUMN_TITLE, title)
                put(COLUMN_DESCRIPTION, desc)
                put(COLUMN_TYPE, type.name)
                put(COLUMN_PARENT_ID, 0)
            }
            db.insert(TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(parentId: Long?, type: ItemType, title: String, description: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_TYPE, type.name)
            put(COLUMN_PARENT_ID, parentId)
            put(COLUMN_DESCRIPTION, description)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun deleteTask(taskId: Long): Int {
        return writableDatabase.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }

    fun getAllTasks(): List<TaskItem> {
        val db = readableDatabase
        val tasks = mutableListOf<TaskItem>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val parentId = if (isNull(getColumnIndexOrThrow(COLUMN_PARENT_ID))) null else getLong(getColumnIndexOrThrow(COLUMN_PARENT_ID))
                val type = ItemType.valueOf(getString(getColumnIndexOrThrow(COLUMN_TYPE)))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val description = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                tasks.add(TaskItem(id, parentId, type, title, description))
            }
            close()
        }
        return tasks
    }
}