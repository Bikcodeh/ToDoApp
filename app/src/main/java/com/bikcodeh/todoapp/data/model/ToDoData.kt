package com.bikcodeh.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String
)
