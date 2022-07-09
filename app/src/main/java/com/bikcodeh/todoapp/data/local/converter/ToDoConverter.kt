package com.bikcodeh.todoapp.data.local.converter

import androidx.room.TypeConverter
import com.bikcodeh.todoapp.data.model.Priority

class ToDoConverter {

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String): Priority {
        return Priority.valueOf(priority)
    }
}