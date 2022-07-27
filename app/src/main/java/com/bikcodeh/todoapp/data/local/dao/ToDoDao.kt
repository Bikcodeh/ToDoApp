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

    @Delete
    suspend fun deleteNote(toDoData: ToDoData)

    @Query("DELETE FROM tododata")
    suspend fun deleteAll()

    @Query("DELETE FROM tododata WHERE id in (:items) ")
    suspend fun deleteItems(items: List<Int>)

    @Query("SELECT * FROM tododata WHERE title LIKE :query")
    fun searchNotes(query: String): Flow<List<ToDoData>>
}