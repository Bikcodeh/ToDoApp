package com.bikcodeh.todoapp.data.repository

import com.bikcodeh.todoapp.data.local.dao.ToDoDao
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.domain.repository.ToDoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToDoRepositoryImpl @Inject constructor(
    private val toDoDao: ToDoDao
) : ToDoRepository {
    override fun getAllNotes(): Flow<List<ToDoData>> {
        return toDoDao.getAllNotes()
    }

    override suspend fun insertNote(toDoData: ToDoData) {
        toDoDao.insertNote(toDoData)
    }
}