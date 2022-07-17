package com.bikcodeh.todoapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bikcodeh.todoapp.data.model.Priority
import com.bikcodeh.todoapp.data.model.parsePriority
import com.bikcodeh.todoapp.data.model.parsePriorityName
import com.bikcodeh.todoapp.domain.repository.DataStoreOperations
import com.bikcodeh.todoapp.util.Constants.PREFERENCES_KEY
import com.bikcodeh.todoapp.util.Constants.PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreOperationsImpl @Inject constructor(context: Context) : DataStoreOperations {

    private val dataStore = context.dataStore

    private object PreferencesKey {
        val sortType = stringPreferencesKey(PREFERENCES_KEY)
    }

    override suspend fun saveSort(sort: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.sortType] = sort
        }
    }

    override fun getSavedSort(): Flow<Priority> {
        return dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw  exception
            }
        }.map { preferences ->
            val priority = preferences[PreferencesKey.sortType] ?: Priority.HIGH.name
            parsePriorityName(priority)
        }
    }
}