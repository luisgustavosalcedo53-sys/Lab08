package com.example.lab08.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC") fun observeTasks(): Flow<List<Task>>
    @Query("SELECT * FROM tasks ORDER BY id DESC") suspend fun getAllTasks(): List<Task>
    @Insert suspend fun insertTask(task: Task)
    @Update suspend fun updateTask(task: Task)
    @Query("UPDATE tasks SET is_completed = NOT is_completed WHERE id = :id") suspend fun toggleCompletion(id: Int)
    @Query("UPDATE tasks SET description = :description WHERE id = :id") suspend fun editDescription(id: Int, description: String)
    @Delete suspend fun deleteTask(task: Task)
    @Query("DELETE FROM tasks") suspend fun deleteAllTasks()
}
