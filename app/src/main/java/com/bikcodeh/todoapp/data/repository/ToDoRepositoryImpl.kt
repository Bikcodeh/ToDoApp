package com.bikcodeh.todoapp.data.repository

import com.bikcodeh.todoapp.data.local.dao.ToDoDao
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.domain.repository.DataStoreOperations
import com.bikcodeh.todoapp.domain.repository.ToDoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToDoRepositoryImpl @Inject constructor(
    private val toDoDao: ToDoDao,
    private val dataStoreOperations: DataStoreOperations
) : ToDoRepository {
    override fun getAllNotes(): Flow<List<ToDoData>> {
        return toDoDao.getAllNotes()
    }

    override suspend fun insertNote(toDoData: ToDoData) {
        toDoDao.insertNote(toDoData)
    }

    override suspend fun updateNote(toDoData: ToDoData) {
        toDoDao.updateNote(toDoData)
    }

    override suspend fun deleteNote(toDoData: ToDoData) {
        toDoDao.deleteNote(toDoData)
    }

    override suspend fun deleteAll() {
        toDoDao.deleteAll()
    }

    override suspend fun saveSort(sort: String) {
        dataStoreOperations.saveSort(sort)
    }

    override fun getSavedSort(): Flow<Priority> {
        return dataStoreOperations.getSavedSort()
    }
}