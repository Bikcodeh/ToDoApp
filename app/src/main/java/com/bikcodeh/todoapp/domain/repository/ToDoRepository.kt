package com.bikcodeh.todoapp.domain.repository

import com.bikcodeh.todoapp.data.model.ToDoData
import kotlinx.coroutines.flow.Flow

interface ToDoRepository {
    fun getAllNotes(): Flow<List<ToDoData>>
    suspend fun insertNote(toDoData: ToDoData)
    suspend fun updateNote(toDoData: ToDoData)
    suspend fun deleteNote(toDoData: ToDoData)
}