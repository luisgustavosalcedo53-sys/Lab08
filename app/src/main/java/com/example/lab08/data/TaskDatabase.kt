package com.example.lab08.data
import android.content.Context
import androidx.room.*
@Database(entities = [Task::class], version = 1, exportSchema = true)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    companion object {
        @Volatile private var instance: TaskDatabase? = null
        fun getDatabase(context: Context): TaskDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, TaskDatabase::class.java, "task_db")
                .build().also { instance = it }
        }
    }
}
