package com.bikcodeh.todoapp.data.model

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

fun parsePriority(priority: String): Priority {
    return when (priority) {
        "High Priority" -> Priority.HIGH
        "Medium Priority" -> Priority.MEDIUM
        "Low Priority" -> Priority.LOW
        else -> Priority.LOW
    }
}

fun parsePriorityName(name: String): Priority {
    return when (name) {
        "HIGH" -> Priority.HIGH
        "MEDIUM", "LOW" -> Priority.LOW
        else -> Priority.LOW
    }
}

fun Priority.toIndex(): Int {
    return when (this) {
        Priority.HIGH -> 0
        Priority.MEDIUM -> 1
        Priority.LOW -> 2
    }
}