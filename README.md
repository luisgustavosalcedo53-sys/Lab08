# Lab08 — Tareas

Laboratorio de Kotlin y Jetpack Compose con Room, MVVM y UDF.

Funciones: agregar tareas, cambiar su estado, eliminar todas, editar descripción y eliminar individualmente. Los dos últimos puntos corresponden al ejercicio 1. Interfaz inspirada en la bandeja de entrada de Todoist.

Abrir esta carpeta en Android Studio, seleccionar Gradle JDK 17, sincronizar y ejecutar `app`. Requiere SDK 34; Android mínimo 24. Ejecutar `./gradlew assembleDebug` para compilar y `./gradlew connectedDebugAndroidTest lintDebug` con un emulador activo para verificar.

La base se llama `task_db`. El proyecto no incluye tareas preinsertadas. Los datos creados por el usuario persisten entre aperturas.

Historial de implementación en Git local. Falta publicar en el repositorio de la cuenta del alumno. No contiene el informe completado.
