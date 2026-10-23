package com.example.lab08
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lab08.data.TaskDatabase
import com.example.lab08.ui.*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = viewModelFactory { initializer { TaskViewModel(TaskDatabase.getDatabase(applicationContext).taskDao()) } }
        setContent { Lab08Theme { TaskScreen(viewModel(factory = factory)) } }
    }
}
