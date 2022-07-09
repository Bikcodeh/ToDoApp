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