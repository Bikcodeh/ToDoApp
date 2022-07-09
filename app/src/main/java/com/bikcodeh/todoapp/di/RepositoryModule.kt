package com.bikcodeh.todoapp.di

import com.bikcodeh.todoapp.data.repository.ToDoRepositoryImpl
import com.bikcodeh.todoapp.domain.repository.ToDoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun providesToDoRepository(toDoRepositoryImpl: ToDoRepositoryImpl): ToDoRepository
}