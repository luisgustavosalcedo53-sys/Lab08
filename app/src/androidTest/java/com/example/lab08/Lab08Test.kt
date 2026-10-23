package com.example.lab08
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.lab08.data.*
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import java.io.File

class Lab08Test {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()
    @Before fun clear() = runBlocking { TaskDatabase.getDatabase(ApplicationProvider.getApplicationContext()).taskDao().deleteAllTasks() }
    @Test fun taskWorkflow() {
        rule.onNodeWithText("Agregar tarea", useUnmergedTree = true).performClick()
        rule.onNodeWithText("Guardar").assertIsNotEnabled()
        rule.onNodeWithText("Descripción").performTextInput("Preparar laboratorio")
        rule.onNodeWithText("Guardar").performClick()
        rule.waitUntil(10000) { rule.onAllNodesWithText("Preparar laboratorio").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithContentDescription("Editar Preparar laboratorio").performClick()
        rule.onNodeWithText("Descripción").performTextReplacement("Revisar arquitectura MVVM")
        rule.onNodeWithText("Guardar").performClick()
        rule.waitUntil(10000) { rule.onAllNodesWithText("Revisar arquitectura MVVM").fetchSemanticsNodes().isNotEmpty() }
        rule.onNode(isToggleable()).performClick()
        rule.onNodeWithText("Completada").assertExists()
        rule.activityRule.scenario.recreate()
        rule.waitUntil(10000) { rule.onAllNodesWithText("Revisar arquitectura MVVM").fetchSemanticsNodes().isNotEmpty() }
        rule.onNodeWithText("Completada").assertExists()
        rule.onNodeWithText("Agregar tarea", useUnmergedTree = true).performClick()
        rule.onNodeWithText("Descripción").performTextInput("Comprobar persistencia con Room")
        rule.onNodeWithText("Guardar").performClick()
        rule.mainClock.advanceTimeBy(1000); rule.waitForIdle(); Thread.sleep(500); capture("lab08-tareas")
        rule.onNodeWithContentDescription("Editar Comprobar persistencia con Room").performClick()
        rule.mainClock.advanceTimeBy(1000); rule.waitForIdle(); Thread.sleep(500); capture("lab08-editar")
        rule.onNodeWithText("Cancelar").performClick()
        rule.onNodeWithContentDescription("Eliminar Revisar arquitectura MVVM").performClick()
        rule.onNodeWithText("Cancelar").performClick()
        rule.onNodeWithText("Revisar arquitectura MVVM").assertExists()
        rule.onNodeWithContentDescription("Eliminar Revisar arquitectura MVVM").performClick()
        rule.onNodeWithText("Eliminar").performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitUntil(10000) { rule.onAllNodesWithText("Revisar arquitectura MVVM").fetchSemanticsNodes().isEmpty() }
        rule.onNodeWithText("Eliminar todas").performClick()
        rule.onNodeWithText("Eliminar").performSemanticsAction(SemanticsActions.OnClick) { it() }
        rule.waitUntil(10000) { rule.onAllNodesWithText("Un espacio para tus pendientes").fetchSemanticsNodes().isNotEmpty() }
    }
    @Test fun roomPersistsAfterReopen() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.deleteDatabase("persistence-test")
        var db = Room.databaseBuilder(context, TaskDatabase::class.java, "persistence-test").build()
        db.taskDao().insertTask(Task(description = "Persistente"))
        val item = db.taskDao().getAllTasks().single()
        db.taskDao().toggleCompletion(item.id)
        db.close()
        db = Room.databaseBuilder(context, TaskDatabase::class.java, "persistence-test").build()
        assertEquals("Persistente", db.taskDao().getAllTasks().single().description)
        assertTrue(db.taskDao().getAllTasks().single().isCompleted)
        db.close(); context.deleteDatabase("persistence-test"); Unit
    }
}
private fun capture(name: String) {
    val inst = InstrumentationRegistry.getInstrumentation()
    val file = File(inst.targetContext.getExternalFilesDir(null), "$name.png")
    file.outputStream().use { inst.uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, it) }
}


