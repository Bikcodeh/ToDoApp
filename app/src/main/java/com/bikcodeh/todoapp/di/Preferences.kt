package com.bikcodeh.todoapp.di

import android.content.Context
import com.bikcodeh.todoapp.data.local.preferences.DataStoreOperationsImpl
import com.bikcodeh.todoapp.domain.repository.DataStoreOperations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Preferences {

    @Provides
    @Singleton
    fun providesDataStoreOperations(@ApplicationContext context: Context): DataStoreOperations =
        DataStoreOperationsImpl(context)
}