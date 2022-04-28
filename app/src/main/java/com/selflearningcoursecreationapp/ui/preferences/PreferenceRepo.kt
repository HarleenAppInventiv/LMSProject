package com.selflearningcoursecreationapp.ui.preferences

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface PreferenceRepo {
    suspend fun savePreference(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getCategory(): Flow<Resource>
    suspend fun myCatogory(map: HashMap<String, Any>): Flow<Resource>
    suspend fun getThemeList(): Flow<Resource>
}