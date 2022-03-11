package com.selflearningcoursecreationapp.data.prefrence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object PreferenceDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SelfLearningApp")


    suspend fun saveString(key: Constants, value: String) {
        val dataStoreKey = stringPreferencesKey(key.toString())
        getAppContext().dataStore.edit {
            it[dataStoreKey] = value
        }
    }

    suspend fun saveInt(key: Constants, value: Int) {
        val dataStoreKey = intPreferencesKey(key.toString())
        getAppContext().dataStore.edit {
            it[dataStoreKey] = value
        }

    }

    suspend fun saveBoolean(key: Constants, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key.toString())
        getAppContext().dataStore.edit {
            it[dataStoreKey] = value
        }

    }

    suspend fun getString(key: Constants): String? {
        val dataStoreKey = stringPreferencesKey(key.toString())
        val preferences: Flow<String?> = getAppContext().dataStore.data.map {
            it[dataStoreKey]
        }
        return preferences.first()

    }

    suspend fun getInt(key: Constants): Int? {
        val dataStoreKay = intPreferencesKey(key.toString())
        val preferences = getAppContext().dataStore.data.first()
        return preferences[dataStoreKay]
    }

    suspend fun getBoolean(key: Constants): Boolean? {
        val dataStoreKay = booleanPreferencesKey(key.toString())
        val preferences = getAppContext().dataStore.data.first()
        return preferences[dataStoreKay]
    }
}