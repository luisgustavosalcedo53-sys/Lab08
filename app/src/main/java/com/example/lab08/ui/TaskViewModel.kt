package com.example.lab08.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab08.data.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UDF: la UI envia eventos; tras cada escritura se publica la lista confirmada por Room.
class TaskViewModel(private val dao: TaskDao) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()
    init { write { } }
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    fun clearError() { _error.value = null }
    private fun write(action: suspend () -> Unit) { viewModelScope.launch {
        try { action(); _tasks.value = dao.getAllTasks() } catch (e: CancellationException) { throw e }
        catch (e: Exception) { _error.value = "No se pudo guardar el cambio. Intenta nuevamente." }
    } }
    fun addTask(description: String) { description.trim().takeIf { it.isNotEmpty() }?.let { text -> write { dao.insertTask(Task(description = text)) } } }
    fun toggleTaskCompletion(task: Task) = write { dao.toggleCompletion(task.id) }
    fun editTask(task: Task, description: String) { description.trim().takeIf { it.isNotEmpty() }?.let { text -> write { dao.editDescription(task.id, text) } } }
    fun deleteTask(task: Task) = write { dao.deleteTask(task) }
    fun deleteAllTasks() = write { dao.deleteAllTasks() }
}
