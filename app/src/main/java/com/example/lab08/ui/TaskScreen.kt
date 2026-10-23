package com.example.lab08.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab08.data.Task

@Composable
fun Lab08Theme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFFCC493A), background = Color(0xFFFAF9F6), surface = Color.White), content = content)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var creating by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<Int?>(null) }
    var deletingAll by rememberSaveable { mutableStateOf(false) }
    val editing = tasks.find { it.id == editingId }
    val deleting = tasks.find { it.id == deletingId }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Lab08", fontWeight = FontWeight.Bold) }, actions = {
            TextButton(onClick = { deletingAll = true }, enabled = tasks.isNotEmpty()) { Text("Eliminar todas") }
        }) },
        floatingActionButton = { ExtendedFloatingActionButton(onClick = { creating = true }, containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White,
            icon = { Icon(Icons.Default.Add, contentDescription = null) }, text = { Text("Agregar tarea") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp)) {
            item {
                Spacer(Modifier.height(20.dp))
                Text("Bandeja de entrada", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("${tasks.count { !it.isCompleted }} pendientes · ${tasks.count { it.isCompleted }} completadas", modifier = Modifier.padding(top = 8.dp, bottom = 24.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (tasks.isEmpty()) item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(28.dp)) {
                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                        Text("Un espacio para tus pendientes", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                        Text("Agrega tu primera tarea. Tus cambios se guardan en este dispositivo.", modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            items(tasks, key = { it.id }) { task ->
                Surface(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(Modifier.padding(vertical = 10.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = task.isCompleted, onCheckedChange = { viewModel.toggleTaskCompletion(task) })
                        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                            Text(task.description, style = MaterialTheme.typography.bodyLarge, textDecoration = if(task.isCompleted) TextDecoration.LineThrough else null)
                            Text(if(task.isCompleted) "Completada" else "Pendiente", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { editingId = task.id }) { Icon(Icons.Default.Edit, "Editar ${task.description}") }
                        IconButton(onClick = { deletingId = task.id }) { Icon(Icons.Default.Delete, "Eliminar ${task.description}") }
                    }
                }
            }
        }
    }
    if (creating) TaskEditor("Nueva tarea", "", { creating = false }) { viewModel.addTask(it); creating = false }
    if (editing != null) TaskEditor("Editar tarea", editing.description, { editingId = null }) { viewModel.editTask(editing, it); editingId = null }
    if (deleting != null || deletingAll) AlertDialog(
        onDismissRequest = { deletingId = null; deletingAll = false },
        title = { Text(if(deletingAll) "¿Eliminar todas las tareas?" else "¿Eliminar tarea?") },
        text = { Text(if(deletingAll) "Se borrarán las ${tasks.size} tareas guardadas." else deleting!!.description) },
        confirmButton = { TextButton(onClick = { if(deletingAll) viewModel.deleteAllTasks() else deleting?.let(viewModel::deleteTask); deletingId = null; deletingAll = false }) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = { deletingId = null; deletingAll = false }) { Text("Cancelar") } }
    )
    if (error != null) AlertDialog(onDismissRequest = viewModel::clearError, title = { Text("No se guardó") }, text = { Text(error!!) }, confirmButton = { TextButton(onClick = viewModel::clearError) { Text("Aceptar") } })
}
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
private fun TaskEditor(title: String, initial: String, dismiss: () -> Unit, save: (String) -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current
    var description by rememberSaveable { mutableStateOf(initial) }
    AlertDialog(onDismissRequest = dismiss, title = { Text(title) }, text = {
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), maxLines = 5)
    }, confirmButton = { TextButton(onClick = { keyboard?.hide(); save(description) }, enabled = description.isNotBlank()) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = dismiss) { Text("Cancelar") } })
}
