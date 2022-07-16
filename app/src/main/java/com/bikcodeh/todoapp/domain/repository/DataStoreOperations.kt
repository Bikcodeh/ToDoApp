package com.bikcodeh.todoapp.domain.repository

import com.bikcodeh.todoapp.data.model.Priority
import kotlinx.coroutines.flow.Flow

interface DataStoreOperations {
    suspend fun saveSort(sort: String)
    fun getSavedSort(): Flow<Priority>
}