package com.bikcodeh.todoapp.data.local.dao

import androidx.room.*
import com.bikcodeh.todoapp.data.model.ToDoData
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Query("SELECT * FROM tododata ORDER BY id ASC")
    fun getAllNotes(): Flow<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(toDoData: ToDoData)

    @Update
    suspend fun updateNote(toDoData: ToDoData)
}