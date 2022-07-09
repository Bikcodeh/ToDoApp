package com.bikcodeh.todoapp.di

import android.content.Context
import androidx.room.Room
import com.bikcodeh.todoapp.data.local.ToDoDatabase
import com.bikcodeh.todoapp.data.local.dao.ToDoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesToDoDatabase(@ApplicationContext context: Context): ToDoDatabase =
        Room.databaseBuilder(
            context,
            ToDoDatabase::class.java,
            "todo_notes.db"
        ).build()

    @Provides
    @Singleton
    fun providesToDoDao(toDoDatabase: ToDoDatabase): ToDoDao = toDoDatabase.todoDao()
}