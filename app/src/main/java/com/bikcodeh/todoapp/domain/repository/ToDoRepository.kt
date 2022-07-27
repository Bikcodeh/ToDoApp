package com.bikcodeh.todoapp.domain.repository

import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.data.model.ToDoData
import kotlinx.coroutines.flow.Flow

interface ToDoRepository {
    fun getAllNotes(): Flow<List<ToDoData>>
    suspend fun insertNote(toDoData: ToDoData)
    suspend fun updateNote(toDoData: ToDoData)
    suspend fun deleteNote(toDoData: ToDoData)
    suspend fun deleteAll()
    suspend fun saveSort(sort: String)
    fun getSavedSort(): Flow<Priority>
    suspend fun deleteItems(items: List<ToDoData>)
    fun searchNotes(query: String): Flow<List<ToDoData>>
}