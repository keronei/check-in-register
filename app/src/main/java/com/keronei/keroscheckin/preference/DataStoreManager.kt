package com.keronei.keroscheckin.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveBooleanPreference(key: String, value: Boolean) {
        dataStore.edit { preference ->
            preference[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun saveStringPreference(key: String, value: String) {
        dataStore.edit { preference ->
            preference[stringPreferencesKey(key)] = value
        }
    }

    fun getBooleanPreference(key: String): Flow<Boolean> =
        dataStore.data
            .catch {
                if (it is IOException) {
                    emit(emptyPreferences())
                }
            }.map { preferences ->

                preferences[booleanPreferencesKey(key)] ?: false

            }


    fun getStringPreference(key: String): Flow<String> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preference ->
        preference[stringPreferencesKey(key)] ?: ""
    }


    suspend fun cleanUpPreferenceAtLogOut() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

   suspend fun saveIntPreference(key: String, id: Int) {
       dataStore.edit { preference ->
           preference[intPreferencesKey(key)] = id
       }
    }

    fun getIntPreference(key: String): Flow<Int> = dataStore.data.catch {
        if (it is IOException) {
            emit(emptyPreferences())
        }
    }.map { preference ->
        preference[intPreferencesKey(key)] ?: -1
    }


}