package com.bikcodeh.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bikcodeh.todoapp.data.local.converter.ToDoConverter
import com.bikcodeh.todoapp.data.local.dao.ToDoDao
import com.bikcodeh.todoapp.data.model.ToDoData

@Database(entities = [ToDoData::class], version = 1, exportSchema = false)
@TypeConverters(ToDoConverter::class)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun todoDao(): ToDoDao
}